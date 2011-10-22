package httpserver;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerLogs {

    private static Logger logger = null;
    private static Level level;

    public static Logger getLoggerInstance() {
        if (logger == null) {
            initialize();
        }
        return logger;
    }

    public static void initialize() {

        String rootCatalog = PropertiesReader.getPropertiesReader().getProperty("ROOT_CATALOG");
        String defaultLoggingLevel = PropertiesReader.getPropertiesReader().getProperty("DEFAULT_LOGGING_LEVEL");

        ServerLogs.setLevel(Level.parse(defaultLoggingLevel));

        logger = Logger.getLogger("Logger");
        FileHandler fh = null;
        try {
            fh = new FileHandler(rootCatalog + "/log.txt", true);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(ServerLogs.class.getName()).log(Level.SEVERE, null, ex);
        }
        fh.setFormatter(new ServerFormatter());
        logger.addHandler(fh);
    }

    public static Level getLevel() {
        return level;
    }

    public static void setLevel(Level level) {
        ServerLogs.level = level;
    }
}
