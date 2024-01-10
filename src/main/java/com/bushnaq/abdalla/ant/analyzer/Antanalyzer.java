package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Uses AntParser to read ant files and analyze which cannot be reached by the given main targets.
 * Uses and AntConsoleOutput to output result to screen
 */
public class Antanalyzer {
    Context context;

    public void start(String[] args) throws IOException {
        String antFile = args[0];//"references/internal-build/build.xml";
        String[] mainTargets = args[1].split(",");//{"build.xml/ris3.msiinstaller.all"};
        context = new Context(antFile, Arrays.asList(mainTargets));
        {
            AntParser antParser = new AntParser(context);
            antParser.loadAntFiles();
        }
        collectAllTargets();
        prepare();
        buildTree();
        {
            AntConsoleOutput antConsoleOutput = new AntConsoleOutput(context);
            antConsoleOutput.printTree();
            antConsoleOutput.printAntFiles();
            antConsoleOutput.printUnusedTargets();
            antConsoleOutput.printStatistics();
            antConsoleOutput.printExceptions();
        }
    }


    void buildTree() {
        markTargets(context, context.folderRoot);
        buildTree(context, context.folderRoot);
    }

    private void buildTree(Context context, String root) {
        List<GlobalTarget> list = AntTools.createGlobalTargetSortedList(context);
        for (GlobalTarget target : list) {
            if (!context.subTargetSet.contains(target.target)) {
                buildTree(context, root, target, target, 0, new IntegerVariable());
            }
        }
    }

    private void buildTree(Context context, String root, GlobalTarget rootGLobalTarget, GlobalTarget globalTarget, int x, IntegerVariable y) {
        //if (rootGLobalTarget.globalTarget.getName().equals("ris.delegationprint.installer.dom.x64"))
        {
            if (globalTarget.used) {
                rootGLobalTarget.tree[y.v][x] = globalTarget.target.getName() + "(" + AntTools.extractFileName(globalTarget.target.getLocation().getFileName()) + ")" + " (used)";
            } else {
                rootGLobalTarget.tree[y.v][x] = globalTarget.target.getName() + "(" + AntTools.extractFileName(globalTarget.target.getLocation().getFileName()) + ")";
            }


            x++;
            y.v++;
            int bx = x;
            int by = y.v;
            boolean child = false;
            for (GlobalTarget subTarget : listDependencies(context, root, globalTarget.target, false)) {
                child = true;
                buildTree(context, root, rootGLobalTarget, subTarget, x, y);
            }
//            if (child)
//                rootGLobalTarget.tree[by - 1][bx - 1] += " (last)";
        }
    }

    private void collectAllTargets() {
        for (Project project : context.projectSet.values()) {
            for (Target target : project.getTargets().values()) {
                if (!target.getName().isEmpty()) {
                    GlobalTarget firstTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getName());
                    if (firstTarget != null) {
                        context.exceptionList.add(new AntException(String.format("target name '%s' already exists at '%s' %d:%d", target.getName(), firstTarget.target.getLocation().getFileName(), firstTarget.target.getLocation().getLineNumber(), firstTarget.target.getLocation().getColumnNumber()), target.getLocation().getFileName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    } else {
                        context.targetMap.put(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + target.getName(), new GlobalTarget(target));
                    }
                }
            }
        }
    }

    private void markTargets(Context context, String root) {
        boolean changed = false;
        do {
            changed = false;
            for (GlobalTarget globalTarget : context.targetMap.values()) {
                if (markTargets(context, AntTools.extractRootFolder(context, globalTarget.target.getLocation().getFileName()), globalTarget))
                    changed = true;
            }
        }
        while (changed);
    }

    private boolean markTargets(Context context, String root, GlobalTarget globalTarget) {
        boolean changed = false;
        if (context.mainTargetSet.contains(globalTarget.target) && !globalTarget.used) {
            context.usedTargetSet.add(globalTarget.target);
            globalTarget.used = true;
            changed = true;
        }
        for (GlobalTarget subTarget : listDependencies(context, root, globalTarget.target, false)) {
            context.subTargetSet.add(subTarget.target);
            if (globalTarget.used) {
                if (!subTarget.used) {
                    subTarget.used = true;
                    context.usedTargetSet.add(subTarget.target);
                    changed = true;
                }
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
    private List<GlobalTarget> listDependencies(Context context, String root, Target target, boolean logExceptions) {
        List<GlobalTarget> dependencies = new ArrayList<>();
        Enumeration<String> dependencEnumeration = target.getDependencies();
        // depends property
        for (String subTarget : Collections.list(dependencEnumeration)) {
            GlobalTarget childGlobalTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
            if (childGlobalTarget != null) {
                dependencies.add(childGlobalTarget);
            } else if (logExceptions) {
                context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
            }
        }
        for (Task task : target.getTasks()) {
            if (task.getTaskType().equals("antcall")) {
                //antcall subtask
                RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
                String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
                GlobalTarget childGlobalTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
                if (childGlobalTarget != null) {
                    dependencies.add(childGlobalTarget);
                } else if (logExceptions) {
                    context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                }
            }
            if (task.getTaskType().equals("ant")) {
                //ant sub task
                File taskAntfile = AntTools.extractSubAntFile(context, AntTools.extractRootFolder(context, target.getLocation().getFileName()), task);
                if (taskAntfile != null) {
                    //target is in new ant file
                    RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
                    String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
                    GlobalTarget childGlobalTarget = context.targetMap.get(taskAntfile.getName() + "/" + subTarget);
                    if (childGlobalTarget != null) {
                        dependencies.add(childGlobalTarget);
                    } else if (logExceptions) {
                        context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    }
                } else {
                    // target is in same ant file
                    RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
                    String subTarget = (String) runtimeConfigurableWrapper.getAttributeMap().get("target");
                    GlobalTarget childGlobalTarget = context.targetMap.get(AntTools.extractFileName(target.getLocation().getFileName()) + "/" + subTarget);
                    if (childGlobalTarget != null) {
                        dependencies.add(childGlobalTarget);
                    } else if (logExceptions) {
                        context.exceptionList.add(new AntException("unknown target", target.getName(), target.getLocation().getLineNumber(), target.getLocation().getColumnNumber()));
                    }
                }
            }
        }
        return dependencies;
    }

}
