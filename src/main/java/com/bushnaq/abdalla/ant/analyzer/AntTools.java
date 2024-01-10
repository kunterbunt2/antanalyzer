package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;

import java.io.File;

public class AntTools {
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

}
