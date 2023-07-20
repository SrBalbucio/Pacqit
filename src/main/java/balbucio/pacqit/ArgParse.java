package balbucio.pacqit;

import lombok.Data;

import java.io.File;

@Data
public class ArgParse {

    private String[] args;
    private boolean consoleOnly = false;
    private File projectDir = null;

    public ArgParse(String[] args) {
        this.args = args;
    }

    private void configure(){
        for(String c : args){
            if(c.equalsIgnoreCase("--console-only")){
                consoleOnly = true;
            } else {
                String[] option = c.split("=");
                if(option[0].equalsIgnoreCase("--projectdir")){
                    projectDir = new File(option[1]);
                }
            }
        }
    }
}
