package me.k11i.jackson;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GenerateModuleVersionFile extends DefaultTask {
    private String packageName;
    private String groupId;
    private String artifactId;
    private String version;

    @Input
    @Optional
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Input
    @Optional
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Input
    @Optional
    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    @Input
    @Optional
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @OutputDirectory
    public File getGeneratedSourceDir() {
        return new File(String.format("%s/build/generated-src/jackson-version", getProject().getProjectDir()));
    }

    @TaskAction
    public void generateVersionFile() {
        String version = this.version != null ? this.version : getProject().getVersion().toString();
        String groupId = this.groupId != null ? this.groupId : getProject().getGroup().toString();
        String artifactId = this.artifactId != null ? this.artifactId : getProject().getName();

        Replacements replacements = buildReplacements(packageName, version, groupId, artifactId);
        generate(packageName, replacements);
    }

    private Replacements buildReplacements(String packageName, String version, String groupId, String artifactId) {
        Replacements result = new Replacements();

        result.addEntry("${package}", packageName)
                .addEntry("${version}", version)
                .addEntry("${groupId}", groupId)
                .addEntry("${artifactId}", artifactId);

        return result;
    }

    private void generate(String packageName, Replacements replacements) {
        try (InputStream in = this.getClass().getResourceAsStream("ModuleVersion.java.template");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {

            try (FileOutputStream fileOut = new FileOutputStream(getGeneratedJavaSourcePath(packageName));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut, "UTF-8"))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    line = replacements.replace(line);
                    writer.write(line);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getGeneratedJavaSourcePath(String packageName) {
        File result = new File(String.format("%s/%s/ModuleVersion.java", getGeneratedSourceDir(), packageName.replace('.', '/')));

        File dir = result.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException(String.format("Cannot create the directory: %s", dir));
            }
        }

        return result;
    }

    static class Replacements {
        static class Entry {
            private final Pattern pattern;
            private final String replacement;

            Entry(Pattern pattern, String replacement) {
                this.pattern = pattern;
                this.replacement = replacement;
            }

            String apply(String text) {
                return pattern.matcher(text).replaceAll(replacement);
            }
        }

        private List<Entry> entries = new ArrayList<>();

        Replacements addEntry(String pattern, String replacement) {
            entries.add(new Entry(Pattern.compile(pattern, Pattern.LITERAL), replacement));
            return this;
        }

        String replace(String line) {
            for (Entry entry : entries) {
                line = entry.apply(line);
            }
            return line;
        }
    }
}
