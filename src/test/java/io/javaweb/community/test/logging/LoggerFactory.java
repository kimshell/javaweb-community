package io.javaweb.community.test.logging;

import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by KevinBlandy on 2018/1/17 10:15
 */
public class LoggerFactory {

    private static Logger LOGGER = null;

    public static Logger getLogger(){
        return LOGGER;
    }

    static {
        LOGGER = Logger.getGlobal();
        //日志级别
        LOGGER.setLevel(Level.ALL);
        LogFileHandler logFileHandler = new LogFileHandler(Paths.get("d:\\my.log"));
//        logFileHandler.setFormatter(new Formatter() {
//            @Override
//            public String format(LogRecord record) {
//                return "[" + record.getLevel() + "]" + "[" + record.getMillis() + "] " + record.getMessage();
//            }
//        });
        try {
            logFileHandler.setEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        LOGGER.addHandler(logFileHandler);
    }
}
