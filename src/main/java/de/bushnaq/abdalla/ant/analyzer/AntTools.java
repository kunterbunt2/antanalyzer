package de.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AntTools {
    public static List<MultiAntTarget> createGlobalTargetSortedList(Context context) {
        List<MultiAntTarget> list = new ArrayList<>(context.targetMap.values());
        Collections.sort(list, new Comparator<MultiAntTarget>() {
            public int compare(MultiAntTarget t1, MultiAntTarget t2) {
                if (t1.isUsed == t2.isUsed) {
                    int file = t1.target.getLocation().getFileName().compareTo(t2.target.getLocation().getFileName());
                    if (file == 0)
                        return t1.target.getName().compareTo(t2.target.getName());
                    else return file;
                }

                if (t1.isUsed /*&& !t2.used*/)
                    return -1;
                else
                    return 1;
            }
        });
        return list;

    }

    public static String extractFileName(String fileName) {
        return new File(fileName).getName();
    }
    public static String extractRootFolder(Context context, String antFile) {
        if (antFile.startsWith(context.absoluteFolderPath)) {
            antFile = antFile.substring(context.absoluteFolderPath.length() + 1);
            antFile = context.folderRoot + "/" + antFile;
        }
        String fixedString = antFile.replace('\\', '/');
        return fixedString.substring(0, fixedString.lastIndexOf("/"));
    }

    public static File extractSubAntFile(Context context, String root, Task task) {
        if (task.getTaskType().equals("ant")) {
            RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
            String taskAntfile = (String) runtimeConfigurableWrapper.getAttributeMap().get("antfile");
            if (taskAntfile != null) {
                //ant target in different ant file
                String subAntFile = context.folderRoot + "/" + taskAntfile;
                if (!new File(subAntFile).exists())
                    subAntFile = root + "/" + taskAntfile;
                File file = new File(subAntFile);
                if (file.exists()) {
                    return file;
                } else {
                    if (!context.missingAntFiles.contains(subAntFile)) {
                        context.missingAntFiles.add(subAntFile);
                        context.exceptionList.add(new AntException("cannot find referenced ant file", subAntFile, task.getLocation().getLineNumber(), task.getLocation().getColumnNumber()));
                    }
                }
            } else {
                //ant target in same ant file
            }
        }
        return null;
    }


}
