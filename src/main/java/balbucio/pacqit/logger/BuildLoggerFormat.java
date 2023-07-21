package balbucio.pacqit.logger;

import balbucio.pacqit.compiler.ProjectBuild;
import de.milchreis.uibooster.components.WaitingDialog;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@AllArgsConstructor
@Data
public class BuildLoggerFormat extends Formatter {
    private ProjectBuild build;
    private WaitingDialog dialog;
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        if(record.getLevel() == Level.FINE){
            builder.append("[BUILD - javac] OUTPUT: ");
            builder.append(formatMessage(record));
        } else {
            builder.append("[").append("BUILD").append("] - ");
            builder.append(formatMessage(record));
        }
        builder.append("\n");
        if(dialog != null) {
            dialog.addToLargeMessage(builder.toString());
        }
        return builder.toString();
    }
}
