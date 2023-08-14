package balbucio.pacqitapp;

import balbucio.pacqitapp.dependencies.DependencyManager;
import balbucio.pacqitapp.page.ProjectCreateDialog;
import balbucio.pacqitapp.task.DependencyIndexTask;
import balbucio.responsivescheduler.ResponsiveScheduler;

public class PacqitApp {

    public static void main(String[] args) {
        new PacqitApp();
    }

    private ResponsiveScheduler scheduler;
    private DependencyManager dependencyManager;

    public PacqitApp(){
        this.dependencyManager = new DependencyManager();
        this.scheduler = new ResponsiveScheduler();
        scheduler.repeatTask(new DependencyIndexTask(dependencyManager), 1000, 1000*60*60);
        new ProjectCreateDialog(null);
    }
}
