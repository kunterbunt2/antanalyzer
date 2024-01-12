package de.bushnaq.abdalla.antanalyzer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Uses AntParser to read ant files and analyze which cannot be reached by the given main targets.
 * Uses and AntConsoleOutput to output result to screen
 */
public class Antanalyzer {
    Context context;

    public void start(String[] args) throws Exception {
        context = new Context();
        AntConsoleOutput antConsoleOutput = new AntConsoleOutput(context);
        ApplicationCli applicationCli = new ApplicationCli(context);
        if( !applicationCli.start(args))
        {
            {
                AntParser antParser = new AntParser(context);
                antConsoleOutput.printString("parsing ant files...");
                antParser.loadAntFiles();
            }
            antConsoleOutput.printString("analyzing targets...");
            collectAllTargets();
            prepare();
            antConsoleOutput.printString("building tree...");
            buildTree();
            {
                if (context.isPrintTree())
                    antConsoleOutput.printTree();
                if (context.isPrintAntFiles())
                    antConsoleOutput.printAntFiles();
                if (context.isPrintUnusedTargets())
                    antConsoleOutput.printUnusedTargets();
                antConsoleOutput.printMainTargets();
                antConsoleOutput.printStatistics();
                antConsoleOutput.printExceptions();
                antConsoleOutput.printSuccess();
            }
        }
    }


    void buildTree() {
        markTargets(context.folderRoot);
        markAntFiles();
        buildTree(context.folderRoot);
    }

    private void buildTree(String root) {
        List<MultiAntTarget> list = AntTools.createGlobalTargetSortedList(context);
        for (MultiAntTarget target : list) {
            if (!target.isSubTarget) {
                buildTree(root, target, target, 0, new IntegerVariable(), list.get(list.size() - 1).equals(target));
            }
        }
    }

    private void buildTree(String root, MultiAntTarget rootGLobalTarget, MultiAntTarget multiAntTarget, int x, IntegerVariable y, boolean isLastChildNode) {
        //if (rootGLobalTarget.globalTarget.getName().equals("ris.delegationprint.installer.dom.x64"))
        {
            TreeNode treeNode = new TreeNode();
            treeNode.isLastChildNode = isLastChildNode;
            treeNode.label = multiAntTarget.target.getName() + " (" + AntTools.extractFileName(multiAntTarget.target.getLocation().getFileName()) + ")";
            treeNode.isUsed = multiAntTarget.isUsed;
            treeNode.isMainNode = multiAntTarget.isMainNode;
            rootGLobalTarget.tree.put(x, y.v, treeNode);

            x++;
            y.v++;
            int bx = x;
            int by = y.v;
            boolean child = false;
            List<MultiAntTarget> list = listDependencies(context, root, multiAntTarget.target, false);
            int c = 0;
            for (MultiAntTarget subTarget : list) {
                buildTree(root, rootGLobalTarget, subTarget, x, y, c == list.size() - 1);
                c++;
            }
        }
    }

    private void collectAllTargets() {
        for (Project project : context.projectSet.values()) {
            for (Target target : project.getTargets().values()) {
                if (!target.getName().isEmpty()) {
                    MultiAntTarget firstTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getName());
                    if (firstTarget != null) {
                        context.exceptionList.add(new AntException(String.format("target name '%s' already exists at '%s' %d:%d", target.getName(), firstTarget.target.getLocation().getFileName(), firstTarget.target.getLocation().getLineNumber(), firstTarget.target.getLocation().getColumnNumber()), target.getLocation().getFileName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    } else {
                        context.targetMap.put(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getName(), new MultiAntTarget(target));
                    }
                }
            }
        }
    }

    private void markAntFiles() {
        for (MultiAntTarget target : context.targetMap.values()) {
            if (target.isUsed) {
                File file = new File(target.target.getLocation().getFileName());
                String antFileName = AntTools.extractRootFolder(context, file.getPath()) + "/" + file.getName();
                context.usedAntFiles.add(antFileName);
            }
        }
        for (String antFile : context.antFileNameSet) {
            if (!context.usedAntFiles.contains(antFile)) {
                context.unusedAntFiles.add(antFile);
            }
        }

    }

    private void markTargets(String root) {
        boolean changed = false;
        do {
            changed = false;
            for (MultiAntTarget multiAntTarget : context.targetMap.values()) {
                if (markTargets(AntTools.extractRootFolder(context, multiAntTarget.target.getLocation().getFileName()), multiAntTarget))
                    changed = true;
            }
        }
        while (changed);
    }

    private boolean markTargets(String root, MultiAntTarget multiAntTarget) {
        boolean changed = false;
        if (context.mainTargetSet.contains(multiAntTarget.target) && !multiAntTarget.isMainNode) {
//            context.usedTargetSet.add(multiAntTarget.target);
            multiAntTarget.isMainNode = true;
            multiAntTarget.isUsed = true;
            changed = true;
        }

        for (MultiAntTarget subTarget : listDependencies(context, root, multiAntTarget.target, false)) {
            subTarget.isSubTarget = true;
//            context.subTargetSet.add(subTarget.target);
            if (multiAntTarget.isUsed && !subTarget.isUsed) {
                subTarget.isUsed = true;
//                context.usedTargetSet.add(subTarget.target);
                changed = true;
            }
        }

        return changed;
    }

    void prepare() {
        context.prepare();
    }


    /**
     * method that extracts a targets dependency to other targets
     * this method will look into
     * the 'depends' property (example: depends="init, product.ris.base))
     * any 'ant' sub task (example: <ant target="ris.base.installer.exp_eu.x86" />)
     * any antcall sub task (example: <antcall target="lib.copy.any"></antcall>)
     *
     * @param context
     * @param root
     * @param target
     * @param logExceptions
     * @return list of all dependent targets
     */
    private List<MultiAntTarget> listDependencies(Context context, String root, Target target, boolean logExceptions) {
        List<MultiAntTarget> dependencies = new ArrayList<>();
        Enumeration<String> dependencEnumeration = target.getDependencies();
        // depends property
        for (String subTarget : Collections.list(dependencEnumeration)) {
            MultiAntTarget childMultiAntTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
            if (childMultiAntTarget != null) {
                dependencies.add(childMultiAntTarget);
            } else if (logExceptions) {
                context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
            }
        }
        for (Task task : target.getTasks()) {
            if (task.getTaskType().equals("antcall")) {
                //antcall subtask
                RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
                String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
                MultiAntTarget childMultiAntTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
                if (childMultiAntTarget != null) {
                    dependencies.add(childMultiAntTarget);
                } else if (logExceptions) {
                    context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                }
            }
            if (task.getTaskType().equals("ant")) {
                //ant sub task
                File taskAntfile = AntTools.extractSubAntFile(context, AntTools.extractRootFolder(context, target.getLocation().getFileName()), task);
                RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
                String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
                if (taskAntfile != null) {
                    //target is in new ant file
                    MultiAntTarget childMultiAntTarget;
                    if (subTarget != null) {
                        childMultiAntTarget = context.targetMap.get(taskAntfile.getName() + "/" + subTarget);
                    } else {
//                        Project mainProject = context.projectSet.get(AntTools.extractRootFolder(context,file.getPath()) + "/" + file.getName());
                        childMultiAntTarget = context.targetMap.get(taskAntfile.getName() + "/" + context.projectSet.get(AntTools.extractRootFolder(context, taskAntfile.getPath()) + "/" + taskAntfile.getName()).getDefaultTarget());
                    }
                    if (childMultiAntTarget != null) {
                        dependencies.add(childMultiAntTarget);
                    } else if (logExceptions) {
                        context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    }
                } else {
                    // target is in same ant file
//                    RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
//                    String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
                    MultiAntTarget childMultiAntTarget;
                    if (subTarget != null) {
                        childMultiAntTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
                    } else {
                        childMultiAntTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getProject().getDefaultTarget());
                    }

                    if (childMultiAntTarget != null) {
                        dependencies.add(childMultiAntTarget);
                    } else if (logExceptions) {
                        context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    }
                }
            }
        }
        return dependencies;
    }

}
