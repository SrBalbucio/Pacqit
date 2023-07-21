package balbucio.pacqit.command.impl;

import balbucio.pacqit.Main;
import balbucio.pacqit.command.Command;

public class OpenSettingsCommand implements Command {
    @Override
    public String getName() {
        return "opensettings";
    }

    @Override
    public void run(String[] args, Main app) {
        app.projectSettingsForm();
    }
}
