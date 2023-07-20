package balbucio.pacqit;

import lombok.Data;

@Data
public class ArgParse {

    private String[] args;
    private boolean consoleOnly = false;

    public ArgParse(String[] args) {
        this.args = args;
    }

    private void configure(){
        for(String c : args){
            if(c.equalsIgnoreCase("--console-only")){
                consoleOnly = true;
            }
        }
    }
}
