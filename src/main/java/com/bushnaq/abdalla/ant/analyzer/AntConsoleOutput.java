package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Project;

import java.util.List;

public class AntConsoleOutput {
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";    // Declaring ANSI_RESET so that we can reset the color
    private static final String ANSI_YELLOW = "\u001B[33m";
    private final Context context;

    protected AntConsoleOutput(Context context) {
        this.context = context;
    }
    protected void printExceptions() {
        System.out.printf("\n%s// exceptions%s\n", ANSI_BLUE, ANSI_RESET);
        int exceptionCount = 0;
        for (AntException e : context.exceptionList) {
            System.out.printf("%s%03d '%s' %s %d:%d%n%s", ANSI_RED, exceptionCount + 1, e.getMessage(), e.file, e.lineNumber, e.columnNumber, ANSI_RESET);
            exceptionCount++;
        }
        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }

    protected void printAntFiles() {
        int maxNameLength = 0;
        for (String antFile : context.antFileNameSet) {
            maxNameLength = Math.max(maxNameLength, antFile.length());
        }

        System.out.printf("\n%s// ant files%s\n", ANSI_BLUE, ANSI_RESET);
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
        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
        if (context.targetMap.values().size() != targetCount)
            context.exceptionList.add(new AntException(String.format("Sum of targets '%d' in all ant files does not match number of target '%d'", targetCount, context.targetMap.values().size())));
    }
    protected void printStatistics() {
        System.out.println();
        System.out.printf("%d targets in %d ant files\n", context.targetMap.keySet().size(), context.projectSet.size());
        printMainTargets();
        System.out.printf("%d used targets in %d ant files\n", context.usedTargetSet.size(), context.projectSet.size());
    }
    protected void printUnusedTargets() {
        System.out.printf("\n%s// unused targets%s\n", ANSI_BLUE, ANSI_RESET);
        List<GlobalTarget> list = AntTools.createGlobalTargetSortedList(context);
        int maxNameLength = 0;
        for (GlobalTarget target : list) {
            if (!target.used) {
                maxNameLength = Math.max(maxNameLength, target.target.getName().length());
            }
        }

        int targetCount = 0;
        for (GlobalTarget target : list) {
            if (!target.used) {
                System.out.printf("%d %-" + maxNameLength + "s defined at %s %d:%d\n", targetCount + 1, target.target.getName(), target.target.getLocation().getFileName(), target.target.getLocation().getLineNumber(), target.target.getLocation().getColumnNumber());
                targetCount++;
            }
        }
        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }
    protected void printTree() {
        int targetMaxCount = 0;

        List<GlobalTarget> list = AntTools.createGlobalTargetSortedList(context);
        for (GlobalTarget target : list) {
            if (!context.subTargetSet.contains(target.target)) {
                targetMaxCount++;
            }
        }
        int targetCount = 0;
        System.out.printf("\n%s// target tree (yellow=unused)%s\n", ANSI_BLUE, ANSI_RESET);
        for (GlobalTarget target : list) {
            if (!context.subTargetSet.contains(target.target)) {
                System.out.printf("\n");
                System.out.printf("%d/%d\n", targetCount + 1, targetMaxCount);
                printTree(target);
                targetCount++;
            }
        }
        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
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
    private void printMainTargets() {
        System.out.printf("\n%s// main ant targets%s\n", ANSI_BLUE, ANSI_RESET);
        int targetCount = 0;
        for (String mainTarget : context.mainTargets) {
            System.out.printf("%3d %s%n", targetCount + 1, mainTarget);
            targetCount++;
        }
        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }



}
