package de.bushnaq.abdalla.antanalyzer;

import de.bushnaq.abdalla.antanalyzer.util.AntTools;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import java.io.File;

public class MultiAntTarget {
    public Target target;
    public boolean isNeeded = false;// if true, then a main target has a direct or indirect dependency to this target.
    public boolean isMainNode = false;// if true, this target is one of the main targets, e.g. cant is called with this target in some use case.
    public AntTree tree = new AntTree();
    public boolean isSubTarget = false;// targets that other targets are dependent on
    public boolean isErroneous;// targets referencing missing ant file

    public MultiAntTarget(Target target) {
        this.target = target;
    }

    public boolean isReferenceingAntFile(Context context, String antFile) {
        String root = AntTools.extractRootFolder(context, new File(target.getLocation().getFileName()).getPath());
        for (Task task : target.getTasks()) {
            File subFile = AntTools.extractSubAntFile(context, root, task, true);
            if (subFile != null) {
                if (antFile.equalsIgnoreCase(AntTools.extractRootFolder(context, subFile.getPath()) + "/" + subFile.getName()))
                    return true;
            }
        }
        return false;
    }
}
