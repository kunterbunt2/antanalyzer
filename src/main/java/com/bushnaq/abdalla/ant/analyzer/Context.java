package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

import java.io.File;
import java.util.*;

public class Context {
    final String antFile;
    public Set<Target> mainTargetSet = new HashSet<>();// all targets used when executing ant as parameter
    public SortedMap<String, GlobalTarget> targetMap = new TreeMap<>();// all targets in all ant files
    public Set<Target> subTargetSet = new HashSet<>();// targets that other targets are dependent on
    public Set<Target> usedTargetSet = new HashSet<>();// targets that are
    public Map<String, Project> projectSet = new HashMap<>();
    public Set<String> antFileNameSet = new TreeSet<>();//set of all ant files
    public Set<String> antFilePathSet = new TreeSet<>();//set of all ant file relative path, only used for error detection
    public Set<String> missingAntFiles = new TreeSet<>();
    public List<AntException> exceptionList = new ArrayList<>();
    String folderRoot;
    String absoluteFolderPath;
    List<String> mainTargets ;// all targets used when executing ant as parameter

    public Context(String antFile, List<String> mainTargets) {
        this.antFile = antFile;
        folderRoot = antFile.substring(0, antFile.lastIndexOf("/"));
        absoluteFolderPath = new File(folderRoot).getAbsolutePath();
        this.mainTargets=mainTargets;
    }

    public void prepare() {
        for (String targetName : mainTargets) {
            GlobalTarget t = targetMap.get(targetName);
            if (t != null)
                mainTargetSet.add(t.target);
        }

    }

}
