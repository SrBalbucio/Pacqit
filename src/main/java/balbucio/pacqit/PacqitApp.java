package balbucio.pacqit;

import balbucio.pacqit.dependencies.DependencyManager;
import balbucio.responsivescheduler.ResponsiveScheduler;

public class PacqitApp {

    public static void main(String[] args) {

    }

    private ResponsiveScheduler scheduler;
    private DependencyManager dependencyManager;

    public PacqitApp(){
        this.dependencyManager = new DependencyManager();

        dependencyManager.loadMavenDependencies();
    }
}
