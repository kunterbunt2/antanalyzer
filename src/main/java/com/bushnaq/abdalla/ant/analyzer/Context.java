package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

import java.io.File;
import java.util.*;

public class Context {
    public String getAntFile() {
        return antFile;
    }

    public void setAntFile(String antFile) {
        this.antFile = antFile;
        folderRoot = antFile.substring(0, antFile.lastIndexOf("/"));
        absoluteFolderPath = new File(folderRoot).getAbsolutePath();
    }

    private String antFile;
    public Set<Target> mainTargetSet = new HashSet<>();// all targets used when executing ant as parameter
    public SortedMap<String, MultiAntTarget> targetMap = new TreeMap<>();// all targets in all ant files
    //    public Set<Target> subTargetSet = new HashSet<>();// targets that other targets are dependent on
//    public Set<Target> usedTargetSet = new HashSet<>();// targets that are
    public Map<String, Project> projectSet = new HashMap<>();
    public Set<String> antFileNameSet = new TreeSet<>();// set of all ant files
    public Set<String> antFilePathSet = new TreeSet<>();// set of all ant file relative path, only used for error detection
    public Set<String> missingAntFiles = new TreeSet<>();// list of ant files referenced but missing
    public List<AntException> exceptionList = new ArrayList<>();// list of all exceptions found during execution
    public Set<String> usedAntFiles = new HashSet<String>();
    public Set<String> unusedAntFiles = new HashSet<String>();
    String folderRoot;
    String absoluteFolderPath;

    public List<String> getMainAntTargets() {
        return mainAntTargets;
    }

    public void setMainAntTargets(List<String> mainAntTargets) {
        this.mainAntTargets.clear();
        File file = new File(antFile);

        for (String target : mainAntTargets) {
            if (!target.contains("/"))
                this.mainAntTargets.add(file.getName() + "/" + target);
            else
                this.mainAntTargets.add(target);
        }
    }

    List<String> mainAntTargets = new ArrayList<>();// all targets used when executing ant as parameter

    public boolean isPrintTree() {
        return printTree;
    }

    public void setPrintTree(boolean printTree) {
        this.printTree = printTree;
    }

    private boolean printTree;

    public boolean isPrintUnusedTargets() {
        return printUnusedTargets;
    }

    public void setPrintUnusedTargets(boolean printUnusedTargets) {
        this.printUnusedTargets = printUnusedTargets;
    }

    private boolean printUnusedTargets;

    public boolean isPrintAntFiles() {
        return printAntFiles;
    }

    public void setPrintAntFiles(boolean printAntFiles) {
        this.printAntFiles = printAntFiles;
    }

    private boolean printAntFiles;

    public Context() {
    }

    public void prepare() {
        for (String targetName : mainAntTargets) {
            MultiAntTarget t = targetMap.get(targetName);
            if (t != null)
                mainTargetSet.add(t.target);
        }

    }

}
