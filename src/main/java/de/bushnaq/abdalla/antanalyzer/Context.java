package de.bushnaq.abdalla.antanalyzer;

import de.bushnaq.abdalla.antanalyzer.util.AntTools;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Context {
    public Set<Target> mainTargetSet = new HashSet<>();// all targets used when executing ant as parameter
    public SortedMap<String, MultiAntTarget> targetMap = new TreeMap<>();// all targets in all ant files
    public Map<String, Project> projectSet = new HashMap<>();
    public Set<String> antFileNameSet = new TreeSet<>();// set of all ant files
    public Set<String> antFilePathSet = new TreeSet<>();// set of all ant file relative path, only used for error detection
    public Set<String> missingAntFiles = new TreeSet<>();// list of ant files referenced but missing
    public List<AntanalyzerException> exceptionList = new ArrayList<>();// list of all exceptions found during execution
    public Set<String> usedAntFiles = new HashSet<String>();
    public Set<String> unusedAntFiles = new HashSet<String>();
    public String folderRoot;
    public String absoluteFolderPath;
    List<String> mainAntTargets = new ArrayList<>();// all targets used when executing ant as parameter
    private String antFile;
    private boolean printTree;
    private boolean printUnusedTargets;
    private boolean printAntFiles;

    public Context() {
    }

    public String getAntFile() {
        return antFile;
    }

    public List<String> getMainAntTargets() {
        return mainAntTargets;
    }

    public boolean isPrintAntFiles() {
        return printAntFiles;
    }

    public boolean isPrintTree() {
        return printTree;
    }

    public boolean isPrintUnusedTargets() {
        return printUnusedTargets;
    }

    public void prepare() {
        for (String targetName : mainAntTargets) {
            MultiAntTarget t = targetMap.get(targetName);
            if (t != null)
                mainTargetSet.add(t.target);
        }

    }

    public void setAntFile(String antFile) throws IOException {
        File file = new File(antFile);
        this.antFile = antFile.replace('\\', '/');
        if (this.antFile.indexOf('/') != -1) {
            folderRoot = this.antFile.substring(0, this.antFile.lastIndexOf("/"));
        }
        else {
            folderRoot = ".";
        }
        absoluteFolderPath = new File(folderRoot).getCanonicalPath();
        this.antFile = AntTools.extractRootFolder(this, file.getPath()) + "/" + file.getName();

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

    public void setPrintAntFiles(boolean printAntFiles) {
        this.printAntFiles = printAntFiles;
    }

    public void setPrintTree(boolean printTree) {
        this.printTree = printTree;
    }

    public void setPrintUnusedTargets(boolean printUnusedTargets) {
        this.printUnusedTargets = printUnusedTargets;
    }

}
