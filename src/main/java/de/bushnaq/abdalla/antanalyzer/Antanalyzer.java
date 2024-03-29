package de.bushnaq.abdalla.antanalyzer;

import de.bushnaq.abdalla.antanalyzer.util.AntTools;
import de.bushnaq.abdalla.antanalyzer.util.IntegerVariable;
import org.apache.tools.ant.*;

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

    void buildTree() {
        markTargets(context.folderRoot);
        markAntFiles();
        markErroneousTargets();
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
            treeNode.isUsed = multiAntTarget.isNeeded;
            treeNode.isErroneous = multiAntTarget.isErroneous;
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

    /**
     * collect all targets of all projects into one multi ant file target list.
     * This list allows targets to habe none unique names if they are located in different ant files.
     */
    private void collectAllTargets() {
        for (Project project : context.projectSet.values()) {
            for (Target target : project.getTargets().values()) {
                if (!target.getName().isEmpty()) {
                    //all targets should be unique
                    MultiAntTarget firstTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getName());
                    if (firstTarget != null) {
                        context.exceptionList.add(new AntanalyzerException(String.format("target name '%s' already exists at '%s' %d:%d", target.getName(), firstTarget.target.getLocation().getFileName(), firstTarget.target.getLocation().getLineNumber(), firstTarget.target.getLocation().getColumnNumber()), target.getLocation().getFileName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    } else {
                        context.targetMap.put(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getName(), new MultiAntTarget(target));
                    }
                }
            }
        }
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
                context.exceptionList.add(new AntanalyzerException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
            }
        }
        for (Task task : target.getTasks()) {
            listDependencies(context, target, logExceptions, task, dependencies);

        }
        return dependencies;
    }

    private static void listDependencies(Context context, Target target, boolean logExceptions, Task task, List<MultiAntTarget> dependencies) {
        if (task.getTaskType().equals("antcall")) {
            //antcall subtask
            RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
            String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
            MultiAntTarget childMultiAntTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
            if (childMultiAntTarget != null) {
                dependencies.add(childMultiAntTarget);
            } else if (logExceptions) {
                context.exceptionList.add(new AntanalyzerException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
            }
        }
        if (task.getTaskType().equals("ant")) {
            //ant sub task
            File taskAntfile = AntTools.extractSubAntFile(context, AntTools.extractRootFolder(context, target.getLocation().getFileName()), task, false);
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
                    context.exceptionList.add(new AntanalyzerException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
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
                    context.exceptionList.add(new AntanalyzerException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                }
            }
        } else if (task.getTaskType().equals("for")) {
            RuntimeConfigurable runtimeConfigurableWrapper1 = task.getRuntimeConfigurableWrapper();
            Enumeration<RuntimeConfigurable> children = runtimeConfigurableWrapper1.getChildren();
            while (children.hasMoreElements()) {
                RuntimeConfigurable rc = children.nextElement();
                if (rc.getElementTag().equalsIgnoreCase("sequential")) {
                    Object proxy1 = rc.getProxy();
                    if (proxy1 instanceof UnknownElement) {
                        Task subtask = ((UnknownElement) proxy1);
                        listDependencies(context, target, logExceptions, subtask, dependencies);
                    }
                }
            }
        } else if (task.getTaskType().equals("sequential")) {
            RuntimeConfigurable runtimeConfigurableWrapper2 = task.getRuntimeConfigurableWrapper();
            Enumeration<RuntimeConfigurable> children = runtimeConfigurableWrapper2.getChildren();
            while (children.hasMoreElements()) {
                RuntimeConfigurable rc = children.nextElement();
                Object proxy1 = rc.getProxy();
                if (proxy1 instanceof UnknownElement) {
                    Task subtask = ((UnknownElement) proxy1);
                    listDependencies(context, target, logExceptions, subtask, dependencies);
                }
            }
        }
    }

    private void markAntFiles() {
        for (MultiAntTarget target : context.targetMap.values()) {
            if (target.isNeeded) {
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

    private void markErroneousTargets() {
        boolean changed = false;
        do {
            changed = false;
            for (MultiAntTarget target : context.targetMap.values()) {
                for (String antFile : context.missingAntFiles) {
                    if (!target.isErroneous && target.isReferenceingAntFile(context, antFile)) {
                        target.isErroneous = true;
                        changed = true;
                    }
                }
                String root = AntTools.extractRootFolder(context, target.target.getLocation().getFileName());
                for (MultiAntTarget subTarget : listDependencies(context, root, target.target, false)) {
                    if (!target.isErroneous && subTarget.isErroneous) {
                        target.isErroneous = true;
                        changed = true;
                    }
                }
            }
        }
        while (changed);
    }

    private void markTargets(String root) {
        boolean changed = false;
        do {
            changed = false;
            // iterate over all targets
            for (MultiAntTarget target : context.targetMap.values()) {
                if (markTargets(AntTools.extractRootFolder(context, target.target.getLocation().getFileName()), target))
                    changed = true;
            }
        }
        while (changed);
    }

    private boolean markTargets(String root, MultiAntTarget target) {
        boolean changed = false;
        if (context.mainTargetSet.contains(target.target) && !target.isMainNode) {
            // this target is member of the main target set, so it is needed
//            context.usedTargetSet.add(multiAntTarget.target);
            target.isMainNode = true;
            target.isNeeded = true;
            changed = true;
        }

        for (MultiAntTarget subTarget : listDependencies(context, root, target.target, false)) {
            subTarget.isSubTarget = true;
//            context.subTargetSet.add(subTarget.target);
            if (target.isNeeded && !subTarget.isNeeded) {
                //subtargets of needed targets are also needed
                subTarget.isNeeded = true;
//                context.usedTargetSet.add(subTarget.target);
                changed = true;
            }
        }

        return changed;
    }

    void prepare() {
        context.prepare();
    }

    public void start(String[] args) throws Exception {
        context = new Context();
        AntanalyzerConsoleOutput antanalyzerConsoleOutput = new AntanalyzerConsoleOutput(context);
        AntanalyzerCli antanalyzerCli = new AntanalyzerCli(context);
        if (!antanalyzerCli.start(args)) {
            {
                AntanalyzerParser antanalyzerParser = new AntanalyzerParser(context);
                antanalyzerConsoleOutput.printString("parsing ant files...");
                antanalyzerParser.loadAntFiles();
            }
            antanalyzerConsoleOutput.printString("analyzing targets...");
            collectAllTargets();
            prepare();
            antanalyzerConsoleOutput.printString("building tree...");
            buildTree();
            {
                if (context.isPrintTree())
                    antanalyzerConsoleOutput.printTree();
                if (context.isPrintUnusedTargets())
                    antanalyzerConsoleOutput.printUnusedTargets();
                if (context.isPrintAntFiles())
                    antanalyzerConsoleOutput.printAntFiles();
                antanalyzerConsoleOutput.printMainTargets();
                antanalyzerConsoleOutput.printStatistics();
                antanalyzerConsoleOutput.printExceptions();
                antanalyzerConsoleOutput.printSuccess();
            }
        }
    }

}
