package kz.edu.fim.notification;
import kz.edu.fim.model.FileEvent; import java.nio.file.*; import java.io.*; import java.nio.charset.StandardCharsets;
public class EventLogger {
 private final Path logFilePath;
 public EventLogger(Path p){logFilePath=p.toAbsolutePath().normalize();}
 public synchronized void log(FileEvent e)throws IOException{
  Files.createDirectories(logFilePath.getParent());
  Files.writeString(logFilePath,format(e)+System.lineSeparator(),StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
 }
 public static String format(FileEvent e){
  String p=e.getFilePath().replace("\\","\\\\").replace("\n","\\n").replace("\r","\\r").replace("\t","\\t").replace("\u001b","\\u001b");
  return e.getDetectedAt()+" "+e.getEventType()+" "+p+" old="+e.getOldHash()+" new="+e.getNewHash();
 }
}
