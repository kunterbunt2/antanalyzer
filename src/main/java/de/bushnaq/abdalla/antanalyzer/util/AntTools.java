package de.bushnaq.abdalla.antanalyzer.util;

import de.bushnaq.abdalla.antanalyzer.AntanalyzerException;
import de.bushnaq.abdalla.antanalyzer.Context;
import de.bushnaq.abdalla.antanalyzer.MultiAntTarget;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.Sequential;

import java.io.File;
import java.util.*;

public class AntTools {
    public static List<MultiAntTarget> createGlobalTargetSortedList(Context context) {
        List<MultiAntTarget> list = new ArrayList<>(context.targetMap.values());
        Collections.sort(list, new Comparator<MultiAntTarget>() {
            public int compare(MultiAntTarget t1, MultiAntTarget t2) {
                if (t1.isNeeded == t2.isNeeded) {
                    int file = t1.target.getLocation().getFileName().compareTo(t2.target.getLocation().getFileName());
                    if (file == 0)
                        return t1.target.getName().compareTo(t2.target.getName());
                    else return file;
                }

                if (t1.isNeeded /*&& !t2.used*/)
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
        if (fixedString.indexOf('/') != -1)
            return fixedString.substring(0, fixedString.lastIndexOf("/"));
        else
            return ".";
    }

    public static File extractSubAntFile(Context context, String root, Task task, boolean returnIfNotExist) {
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
                        context.exceptionList.add(new AntanalyzerException("cannot find referenced ant file", subAntFile, task.getLocation().getLineNumber(), task.getLocation().getColumnNumber()));
                    }
                    if (returnIfNotExist)
                        return file;
                }
            } else {
                //ant target in same ant file
            }
        } else if (task.getTaskType().equals("for")) {
            RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
            Enumeration<RuntimeConfigurable> children = runtimeConfigurableWrapper.getChildren();
            while (children.hasMoreElements()) {
                RuntimeConfigurable rc = children.nextElement();
                if (rc.getElementTag().equalsIgnoreCase("sequential")) {
                    Object proxy1 = rc.getProxy();
                    if (proxy1 instanceof UnknownElement) {

                        Task subtask = ((UnknownElement) proxy1);
                        return extractSubAntFile(context, root, subtask, returnIfNotExist);
                    }
                }
            }
        } else if (task.getTaskType().equals("sequential")) {
            RuntimeConfigurable runtimeConfigurableWrapper = task.getRuntimeConfigurableWrapper();
            Enumeration<RuntimeConfigurable> children = runtimeConfigurableWrapper.getChildren();
            while (children.hasMoreElements()) {
                RuntimeConfigurable rc = children.nextElement();
                Object proxy1 = rc.getProxy();
                if (proxy1 instanceof UnknownElement) {
                    Task subtask = ((UnknownElement) proxy1);
                    return extractSubAntFile(context, root, subtask, returnIfNotExist);
                }
            }
        }
        return null;
    }


}
