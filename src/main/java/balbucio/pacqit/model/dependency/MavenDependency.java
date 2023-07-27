package balbucio.pacqit.model.dependency;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MavenDependency implements Dependency{

    private String pckg;
    private String artifact;
    private String version;
    private long uses;
    private List<String> dependencies = new ArrayList<>();

    public MavenDependency(String pckg, String artifact, String version, long uses) {
        this.pckg = pckg;
        this.artifact = artifact;
        this.version = version;
        this.uses = uses;
    }

    @Override
    public String getPackage() {
        return pckg;
    }

    @Override
    public String getName() {
        return artifact;
    }

    @Override
    public String getLogo() {
        return "maven.png";
    }

    @Override
    public String getToolName() {
        return "Maven";
    }

    @Override
    public String getLanguage() {
        return "Java";
    }
}
