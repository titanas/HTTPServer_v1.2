/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * @author Justas
 */
public class ServerFormatter extends Formatter {

    // This method is called for every log records
    public String format(LogRecord rec) {
        String message = "";
        
        SimpleDateFormat dateFormat = 
                new SimpleDateFormat("EEE, yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(rec.getMillis());
        
        message += dateFormat.format(date);
        
        String level = " [" + rec.getLevel().toString() + "]";
        String spaces = "                    ";
        level += spaces.substring(0, 12-level.length());
        
        message += level;
        message += rec.getMessage();
        message += "\r\n";
        
        return message;
    }
}