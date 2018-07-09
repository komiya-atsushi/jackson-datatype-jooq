package me.k11i.jackson;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.io.File;
import java.util.Set;

public class JacksonVersionFilePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        GenerateModuleVersionFile task = project.getTasks().create("generateJacksonVersionFile", GenerateModuleVersionFile.class);
        project.getTasks().getByName("compileJava").dependsOn(task);

        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceDirectorySet mainJava = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava();

        Set<File> srcDirs = mainJava.getSrcDirs();
        srcDirs.add(task.getGeneratedSourceDir());
        mainJava.setSrcDirs(srcDirs);
    }
}
