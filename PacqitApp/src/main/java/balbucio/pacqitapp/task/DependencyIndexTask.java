package balbucio.pacqitapp.task;

import balbucio.pacqitapp.dependencies.DependencyManager;
import balbucio.responsivescheduler.RSTask;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DependencyIndexTask extends RSTask {

    private DependencyManager manager;

    @Override
    public void run() {
        manager.loadMavenDependencies();
    }
}
