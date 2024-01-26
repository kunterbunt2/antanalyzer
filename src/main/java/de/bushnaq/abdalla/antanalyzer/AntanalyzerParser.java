package de.bushnaq.abdalla.antanalyzer;

import de.bushnaq.abdalla.antanalyzer.util.AntTools;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;

public class AntanalyzerParser {

    Context context;

    public AntanalyzerParser(Context context) {
        this.context = context;

    }

    void loadAntFiles() throws IOException {
        loadAntFiles( context.folderRoot, context.getAntFile());
        if (context.mainAntTargets.isEmpty()) {
            // use default target of main ant file.
            File file = new File(context.getAntFile());
            String antFilePath = AntTools.extractRootFolder(context, file.getPath()) + "/" + file.getName();
            Project mainProject = context.projectSet.get(antFilePath);
            if (mainProject != null) {
                context.mainAntTargets.add(file.getName() + "/" + mainProject.getDefaultTarget());
            } else {
                context.exceptionList.add(new AntanalyzerException("did nto found main ant file"));
            }
        }

    }

    /**
     * load the main ant file and any ant file referenced by that main ant file.
     * this method will look into:
     * any 'ant' sub task (example: <ant antfile="common/build-setup.xml" target="build.copy.x86" />)
     *
     * @param root
     * @param antFile
     * @throws IOException
     */
    private void loadAntFiles(String root, String antFile) throws IOException {
        File file = new File(antFile);
        if (context.antFileNameSet.contains(AntTools.extractRootFolder(context, file.getPath()) + "/" + file.getName())) {
            if (!context.antFilePathSet.contains(file.getPath())) {
                context.exceptionList.add(new AntanalyzerException("Ant file name already used", file.getPath(), 0, 0));
            }
        } else {
            context.antFileNameSet.add(AntTools.extractRootFolder(context, file.getPath()) + "/" + file.getName());
        }
        Project project = new Project();
        context.projectSet.put(antFile.replace('\\', '/'), project);
        project.init();

        ProjectHelper helper = ProjectHelper.getProjectHelper();
        helper.parse(project, file);
        for (String targetName : project.getTargets().keySet()) {
            Target target = project.getTargets().get(targetName);
            for (Task task : target.getTasks()) {
                File subFile = AntTools.extractSubAntFile(context, root, task, false);
                if (subFile != null) {
                    if (context.antFileNameSet.contains(AntTools.extractRootFolder(context, subFile.getPath()) + "/" + subFile.getName())) {
                    } else {
                        loadAntFiles( AntTools.extractRootFolder(context, subFile.getPath()), subFile.getPath());
                    }
                }
            }
        }
    }

}
