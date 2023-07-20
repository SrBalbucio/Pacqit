package balbucio.pacqit.logger;

import balbucio.pacqit.compiler.ProjectBuild;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@AllArgsConstructor
@Data
public class BuildLoggerFormat extends Formatter {

    private static final DateFormat df = new SimpleDateFormat("hh:mm");
    private ProjectBuild build;

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        if(record.getLevel() == Level.FINE){
            builder.append("[BUILD - javac] OUTPUT: ");
            builder.append(formatMessage(record));
        } else {
            builder.append(df.format(new Date(record.getMillis()))).append(" ");
            builder.append("[").append("BUILD").append("] - ");
            builder.append(formatMessage(record));
        }
        builder.append("\n");
        return builder.toString();
    }
}
