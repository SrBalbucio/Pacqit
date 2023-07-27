package balbucio.pacqitapp.model.dependency;

import java.util.List;

public interface DependencyReceiver {

    List<String> getDependencies();
    void addDependency(String dependecyParam);
}
