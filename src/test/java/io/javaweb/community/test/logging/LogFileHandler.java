package io.javaweb.community.test.logging;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by KevinBlandy on 2018/1/17 10:17
 */
public class LogFileHandler extends Handler {

    private BufferedWriter bufferedWriter = null;

    public LogFileHandler(Path path)  {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path.toFile(),true);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(LogRecord record) {
        try {
            this.bufferedWriter.write("[" + record.getLevel() + "]" + " [" + record.getMillis() + "]    " + record.getMessage());
            this.bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        try {
            this.bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws SecurityException {
        try {
            this.bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}