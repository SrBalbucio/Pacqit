package balbucio.pacqit.command;

import balbucio.pacqit.Main;

public interface Command {

    String getName();
    void run(String[] args, Main app);
}
