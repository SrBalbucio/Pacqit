package balbucio.pacqitapp.dependencies.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggerFormat extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append("[").append(record.getLevel().getName()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }
}
