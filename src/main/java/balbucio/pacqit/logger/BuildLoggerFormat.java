package balbucio.pacqit.logger;

import balbucio.pacqit.compiler.ProjectBuild;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@AllArgsConstructor
@Data
public class BuildLoggerFormat extends Formatter {
    private ProjectBuild build;

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
        return builder.toString();
    }
}
