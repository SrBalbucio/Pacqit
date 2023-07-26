package balbucio.pacqit.model.dependency;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GradleDependency implements Dependency{

    private String name;
    private String pckg;
    private String version;
    private long uses;

    public GradleDependency(String name, String pckg, String version, long uses) {
        this.name = name;
        this.pckg = pckg;
        this.version = version;
        this.uses = uses;
    }

    @Override
    public String getPackage() {
        return pckg;
    }

    @Override
    public String getLogo() {
        return "gradle.png";
    }

    @Override
    public String getToolName() {
        return "Gradle";
    }
}
