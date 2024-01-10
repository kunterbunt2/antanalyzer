package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AntAnalyzer {
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";    // Declaring ANSI_RESET so that we can reset the color
    public static final String ANSI_YELLOW = "\u001B[33m";
    Context context;
    public void start(String[] args) throws IOException {
        String[] mainTargets = {"build.xml/ris3.msiinstaller.all"};
        String antFile = "references/internal-build/build.xml";
        context = new Context(antFile, mainTargets);
        AntParser antParser = new AntParser(context);
        antParser.loadAntFiles();
        collectAllTargets();
        prepare();
        buildTree();

    }
    private static void printExceptions(Context context) {
        System.out.printf("\n%s// ant files start ----------------------------------------------------------------------%s\n", ANSI_BLUE, ANSI_RESET);
        int exceptionCount = 0;
        for (AntException e : context.exceptionList) {
            System.out.printf("%s%03d '%s' %s %d:%d%n%s", ANSI_RED, exceptionCount, e.getMessage(), e.file, e.lineNumber, e.columnNumber, ANSI_RESET);
            exceptionCount++;
        }
        System.out.printf("%s--------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }

    void buildTree() {
        markTargets(context, context.folderRoot);
        buildTree(context, context.folderRoot);
        printAntFiles();
        printUnusedTargets();
        printStatistics();
        printExceptions(context);
    }

    private void buildTree(Context context, String root) {
        {
            int targetMaxCount = 0;

            List<GlobalTarget> list = createGlobalTargetSortedList();
            for (GlobalTarget target : list) {
                if (!context.subTargetSet.contains(target.target)) {
                    targetMaxCount++;
                }
            }
            int targetCount = 0;
            for (GlobalTarget target : list) {
                if (!context.subTargetSet.contains(target.target)) {
                    buildTree(context, root, target, target, 0, new IntegerVariable());
                    System.out.printf("\n");
                    System.out.printf("%d/%d\n", targetCount + 1, targetMaxCount);
                    printTree(target);
                    targetCount++;
                }
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

    private List<GlobalTarget> createGlobalTargetSortedList() {
        List<GlobalTarget> list = new ArrayList<>(context.targetMap.values());
        Collections.sort(list, new Comparator<GlobalTarget>() {
            public int compare(GlobalTarget t1, GlobalTarget t2) {
                if (t1.used == t2.used) {
                    int file = t1.target.getLocation().getFileName().compareTo(t2.target.getLocation().getFileName());
                    if (file == 0)
                        return t1.target.getName().compareTo(t2.target.getName());
                    else return file;
                }

                if (t1.used /*&& !t2.used*/)
                    return -1;
                else
                    return 1;
            }
        });
        return list;

    }

    private void markTargets(Context context, String root) {
        boolean changed = false;
        do {
            changed = false;
            for (GlobalTarget globalTarget : context.targetMap.values()) {
                if (markTargets(context, AntTools.extractRootFolder(context,globalTarget.target.getLocation().getFileName()), globalTarget))
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

    private void printAntFiles() {
        int maxNameLength = 0;
        for (String antFile : context.antFileNameSet) {
            maxNameLength = Math.max(maxNameLength, antFile.length());
        }

        System.out.printf("\n%s// ant files start ----------------------------------------------------------------------%s\n", ANSI_BLUE, ANSI_RESET);
        String format = "#   %-" + maxNameLength + "s targets%n";
        System.out.printf(format, "Ant file name");
        int antFileCount = 0;
        int targetCount = 0;
        for (String antFile : context.antFileNameSet) {
            Project p = context.projectSet.get(antFile);
            System.out.printf("%3d %-" + maxNameLength + "s %4d%n", antFileCount, antFile, p.getTargets().size());
            antFileCount++;
            targetCount += p.getTargets().size();
        }
        System.out.printf("%3d %-" + maxNameLength + "s %4d%n", antFileCount, "sum of all ant files", context.targetMap.values().size());
        System.out.printf("%s--------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
        if (context.targetMap.values().size() != targetCount)
            context.exceptionList.add(new AntException(String.format("Sum of targets '%d' in all ant files does not match number of target '%d'",targetCount,context.targetMap.values().size())));
    }

    void printStatistics() {
        System.out.println();
        System.out.printf("%d main targets in %d antfiles\n", context.targetMap.keySet().size() - context.subTargetSet.size(), context.projectSet.size());
        System.out.printf("%d used targets in %d antfiles\n", context.usedTargetSet.size(), context.projectSet.size());

    }

    private void printTree(GlobalTarget target) {
        int maxX = 0;
        int maxY = 0;
        for (int x = 0; x < GlobalTarget.MAX_X; x++) {
            int maxColumnWidth = 0;
            for (int y = 0; y < GlobalTarget.MAX_Y; y++) {
                if (target.tree[y][x] != null) {
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                    target.lineSize[y] = Math.max(target.lineSize[y], x);
                }
            }
        }

        for (int y = 0; y <= maxY; y++) {
            String line = "";
            for (int x = 0; x <= maxX; x++) {
                if (target.tree[y][x] != null) {
                    line += "├── ";

                    if (target.tree[y][x].contains(" (used)")) {
//                        line += ANSI_BLUE;
                        target.tree[y][x] = target.tree[y][x].replace(" (used)", "");
                    } else {
                        line += ANSI_YELLOW;
                    }
                    line += target.tree[y][x];
                    line += ANSI_RESET;
                } else {
                    if (target.lineSize[y] > x) {
                        line += "│   ";
                    }
                }
            }
            System.out.printf("%s\n", line);
        }
    }

    private void printUnusedTargets() {
        System.out.printf("\n%s// unused targets start ----------------------------------------------------------------------%s\n", ANSI_BLUE, ANSI_RESET);
        List<GlobalTarget> list = createGlobalTargetSortedList();
        int maxNameLength = 0;
        for (GlobalTarget target : list) {
            if (!target.used) {
                maxNameLength = Math.max(maxNameLength, target.target.getName().length());
            }
        }

        int targetCount = 0;
        for (GlobalTarget target : list) {
            if (!target.used) {
                System.out.printf("%d %-" + maxNameLength + "s defined at %s %d:%d\n", targetCount, target.target.getName(), target.target.getLocation().getFileName(), target.target.getLocation().getLineNumber(), target.target.getLocation().getColumnNumber());
                targetCount++;
            }
        }
        System.out.printf("%s--------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
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
                File taskAntfile = AntTools.extractSubAntFile(context, AntTools.extractRootFolder(context,target.getLocation().getFileName()), task);
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
