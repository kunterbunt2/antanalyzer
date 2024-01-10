package com.bushnaq.abdalla.ant.analyzer;

import org.apache.tools.ant.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AntParser {

    Context context;

    public AntParser(Context context) {
        this.context = context;

    }

//    public static void main(String[] args) throws IOException {
//        AntParser antParser = new AntParser();
//        antParser.loadAntFiles("references/build/build.xml");
//        antParser.prepare();
//        antParser.buildTree();
//    }




    void loadAntFiles() throws IOException {
        loadAntFiles(context, context.folderRoot, context.antFile);

    }

    /**
     * load the main ant file and any ant file referenced by that main ant file
     * this method will look into
     * any 'ant' sub task (example: <ant antfile="common/build-setup.xml" target="build.copy.x86" />)
     *
     * @param context
     * @param root
     * @param antFile
     * @throws IOException
     */
    private void loadAntFiles(Context context, String root, String antFile) throws IOException {
        File file = new File(antFile);
        if (context.antFileNameSet.contains(AntTools.extractRootFolder(context,file.getPath()) + "/" + file.getName())) {
            if (!context.antFilePathSet.contains(file.getPath())) {
                context.exceptionList.add(new AntException("Ant file name already used", file.getPath(), 0, 0));
            }
        } else {
            context.antFileNameSet.add(AntTools.extractRootFolder(context,file.getPath()) + "/" + file.getName());
        }
        Project project = new Project();
        context.projectSet.put(antFile.replace('\\', '/'), project);
        project.init();

        ProjectHelper helper = ProjectHelper.getProjectHelper();
        helper.parse(project, file);
        for (String targetName : project.getTargets().keySet()) {
            Target target = project.getTargets().get(targetName);
            for (Task task : target.getTasks()) {
                File subFile = AntTools.extractSubAntFile(context, root, task);
                if (subFile != null) {
                    if (context.antFileNameSet.contains(AntTools.extractRootFolder(context,subFile.getPath()) + "/" + subFile.getName()/*subFile.getName()*/)) {
                    } else {
                        loadAntFiles(context, AntTools.extractRootFolder(context,subFile.getPath()), subFile.getPath());
                    }
                }
            }
        }
    }

}
