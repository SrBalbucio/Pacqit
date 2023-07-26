package balbucio.pacqit.model.dependency;

public interface Dependency {

    String getPackage();
    String getName();
    String getVersion();
    long getUses();
    String getLogo();
    String getToolName();
}
