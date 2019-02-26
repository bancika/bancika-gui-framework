package org.diylc.appframework.miscutils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

/**
 * This is a customized log4j appender, which will create a new file for every run of the
 * application.
 */
public class NewLogForEachRunFileAppender extends FileAppender {
  
  // delete after 7 days
  private double deleteOlderThan = 1000 * 60 * 60 * 24 * 7;

  public NewLogForEachRunFileAppender() {}

  public NewLogForEachRunFileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize)
      throws IOException {
    super(layout, filename, append, bufferedIO, bufferSize);
  }

  public NewLogForEachRunFileAppender(Layout layout, String filename, boolean append) throws IOException {
    super(layout, filename, append);
  }

  public NewLogForEachRunFileAppender(Layout layout, String filename) throws IOException {
    super(layout, filename);
  }

  public void activateOptions() {
    if (fileName != null) {
      try {
        fileName = getNewLogFileName();
        setFile(fileName, fileAppend, bufferedIO, bufferSize);
      } catch (Exception e) {
        errorHandler.error("Error while activating log options", e, ErrorCode.FILE_OPEN_FAILURE);
      }
    }
  }

  private String getNewLogFileName() {
    if (fileName != null) {
      String toret = "";
      final String DOT = ".";
      final String HIPHEN = "-";
      final File logFile = new File(fileName);
      final String fileName = logFile.getName();
      String newFileName = "";
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss");
      final int dotIndex = fileName.indexOf(DOT);
      if (dotIndex != -1) {
        // the file name has an extension. so, insert the time stamp
        // between the file name and the extension
        newFileName =
            fileName.substring(0, dotIndex) + HIPHEN + df.format(new java.util.Date()) + DOT + fileName.substring(dotIndex + 1);
      } else {
        // the file name has no extension. So, just append the timestamp
        // at the end.
        newFileName = fileName + HIPHEN + df.format(new java.util.Date());
      }      
      if (logFile.getParentFile().exists()) {
        if (deleteOlderThan > 0) {
          for (File f : logFile.getParentFile().listFiles()) {
            double age = System.currentTimeMillis() - f.lastModified();
            if (age > deleteOlderThan)
              f.delete();
          }
        }        
      } else {
        logFile.getParentFile().mkdirs();        
      }
      toret = logFile.getParent() + File.separator + newFileName;
      return toret;// .replaceAll(":", ".");
    }
    return null;
  }
}
