package balbucio.pacqit.model.dependency;

import java.util.List;

public interface Dependency {

    String getPackage();
    String getName();
    String getVersion();
    long getUses();
    String getLogo();
    String getToolName();
    String getLanguage();
    List<String> getDependencies();
}
