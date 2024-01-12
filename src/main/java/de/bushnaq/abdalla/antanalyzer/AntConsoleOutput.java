package de.bushnaq.abdalla.antanalyzer;

import org.apache.tools.ant.Project;

import java.util.List;

public class AntConsoleOutput {
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";    // Declaring ANSI_RESET so that we can reset the color
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_GRAY = "\u001B[37m";

    private final Context context;
    //    List<Boolean> connected = new ArrayList<Boolean>();
    public Boolean[] connected = new Boolean[1000];

    protected AntConsoleOutput(Context context) {
        this.context = context;
    }

    protected void printAntFiles() {
        int maxNameLength = 0;
        for (String antFile : context.antFileNameSet) {
            maxNameLength = Math.max(maxNameLength, antFile.length()+5);
        }

        System.out.printf("\n%s# ant files%s\n", ANSI_BLUE, ANSI_RESET);
        String format = "#       %-" + maxNameLength + "s targets%n";
        System.out.printf(format, "Ant file name");
        int antFileCount = 0;
        int targetCount = 0;
        for (String antFile : context.antFileNameSet) {
            Project p = context.projectSet.get(antFile);
            if (context.usedAntFiles.contains(antFile))
                System.out.printf("%3d     %-" + maxNameLength + "s %4d%n", antFileCount, antFile, p.getTargets().size());
            else
                System.out.printf("%3d %s[X] %-" + maxNameLength + "s%s %4d%n", antFileCount, ANSI_YELLOW, antFile, ANSI_RESET, p.getTargets().size());
            antFileCount++;
            targetCount += p.getTargets().size();
        }
        System.out.printf("%3d     %-" + maxNameLength + "s %4d%n", antFileCount, "sum of all ant files", context.targetMap.values().size());
//        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
//        if (context.targetMap.values().size() != targetCount)
//            context.exceptionList.add(new AntException(String.format("Sum of targets '%d' in all ant files does not match number of target '%d'", targetCount, context.targetMap.values().size())));
    }

    protected void printExceptions() {
        if(!context.exceptionList.isEmpty())
        {
            System.out.printf("\n%s# exceptions%s\n", ANSI_BLUE, ANSI_RESET);
            int exceptionCount = 0;
            for (AntException e : context.exceptionList) {
                System.out.printf("%s%03d '%s' %s %d:%d%n%s", ANSI_RED, exceptionCount + 1, e.getMessage(), e.file, e.lineNumber, e.columnNumber, ANSI_RESET);
                exceptionCount++;
            }
//            System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
        }
    }

    void printMainTargets() {
        System.out.printf("\n%s# main ant targets%s\n", ANSI_BLUE, ANSI_RESET);
        int targetCount = 0;
        for (String mainTarget : context.mainAntTargets) {
            System.out.printf("%3d %s%n", targetCount + 1, mainTarget);
            targetCount++;
        }
//        System.out.printf("%s# --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }

    protected void printStatistics() {
        System.out.printf("\n%s# statistics%s\n", ANSI_BLUE, ANSI_RESET);
        System.out.printf("%3d targets in %d ant file(s)\n", context.targetMap.keySet().size(), context.projectSet.size());
        int usedTargetCount = 0;
        for(MultiAntTarget target:context.targetMap.values())
        {
            if(target.isUsed)
                usedTargetCount++;
        }
        System.out.printf("%3d used targets in %d ant file(s)\n", usedTargetCount, context.usedAntFiles.size());
    }

    protected void printString(String line) {
        System.out.printf("%s\n", line);
    }

    protected void printSuccess() {
        System.out.printf("\n%sSuccess%s\n", ANSI_GREEN, ANSI_RESET);
    }

    private void printTree(MultiAntTarget target) {

        for (int y = 0; y < target.tree.size(); y++) {
            String line = "";
            for (int x = 0; x < target.tree.size(y); x++) {
                TreeNode node = target.tree.get(x, y);
                if (node != null) {
                    line +=ANSI_GRAY;
                    if (node.isLastChildNode) {
                        line += "└── ";
                        connected[x] = false;
                    } else {
                        line += "├── ";
                        connected[x] = true;
                    }
                    line += ANSI_RESET;

                    if (node.isMainNode) {
                        line += ANSI_GREEN;
                        line += "";
                    } else if (node.isUsed) {
                        line += ANSI_BLUE;
                        line += "";
                    } else {
                        line += ANSI_YELLOW;
                        line += "[X] ";
                    }
                    line += node.label;
                    line += ANSI_RESET;
                } else {
                    line +=ANSI_GRAY;
                    if (connected[x]) {
                        line += "│   ";
                    } else {
                        line += "    ";
                    }
                    line += ANSI_RESET;
                }
            }
            System.out.printf("%s\n", line);
        }
    }

    protected void printTree() {
        int targetMaxCount = 0;

        List<MultiAntTarget> list = AntTools.createGlobalTargetSortedList(context);
        for (MultiAntTarget target : list) {
            if (!target.isSubTarget) {
                targetMaxCount++;
            }
        }
        int targetCount = 0;
        System.out.printf("\n%s# target tree (yellow=unused, blue=used, green=main)%s\n", ANSI_BLUE, ANSI_RESET);
        for (MultiAntTarget target : list) {
            if (!target.isSubTarget) {
                System.out.printf("\n");
                System.out.printf("%d/%d\n", targetCount + 1, targetMaxCount);
                printTree(target);
                targetCount++;
            }
        }
//        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }

    protected void printUnusedTargets() {
        System.out.printf("\n%s# unused targets%s\n", ANSI_BLUE, ANSI_RESET);
        List<MultiAntTarget> list = AntTools.createGlobalTargetSortedList(context);
        int maxNameLength = 0;
        for (MultiAntTarget target : list) {
            if (!target.isUsed) {
                maxNameLength = Math.max(maxNameLength, target.target.getName().length());
            }
        }

        int targetCount = 0;
        for (MultiAntTarget target : list) {
            if (!target.isUsed) {
                System.out.printf("%3d %s[X] %-" + maxNameLength + "s%s defined at %s %d:%d\n", targetCount + 1, ANSI_YELLOW, target.target.getName(), ANSI_RESET, target.target.getLocation().getFileName(), target.target.getLocation().getLineNumber(), target.target.getLocation().getColumnNumber());
                targetCount++;
            }
        }
//        System.out.printf("%s// --------------------------------------------------------------------------------\n%s", ANSI_BLUE, ANSI_RESET);
    }


}
