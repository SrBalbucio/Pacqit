package balbucio.pacqit;

import balbucio.pacqit.model.ProjectAction;
import lombok.Data;

import java.io.File;

@Data
public class ArgParse {

    private String[] args;
    private boolean consoleOnly = false;
    private boolean verbose = false;
    private boolean compileDebug = false;
    private File projectDir = null;
    private ProjectAction action = ProjectAction.NONE;

    // ACTION


    public ArgParse(String[] args) {
        this.args = args;
    }

    private void configure(){
        for(String c : args){
            if(c.equalsIgnoreCase("--console-only")){
                consoleOnly = true;
            } else if(c.equalsIgnoreCase("--verbose")){
                verbose = true;
            } else if(c.equalsIgnoreCase("--compile-debug")){
                compileDebug = true;
            } else if (c.equalsIgnoreCase("--compile")){
                action = ProjectAction.COMPILE;
            } else if(c.equalsIgnoreCase("--build")){
                action = ProjectAction.BUILD;
            }else {
                String[] option = c.split("=");
                if(option[0].equalsIgnoreCase("--projectdir")){
                    projectDir = new File(option[1]);
                }
            }
        }
    }
}
