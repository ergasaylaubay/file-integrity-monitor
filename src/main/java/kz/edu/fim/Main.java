package kz.edu.fim;
import kz.edu.fim.service.*; import kz.edu.fim.repository.*; import kz.edu.fim.notification.*; import kz.edu.fim.model.*;
import java.nio.file.*; import java.util.*; import java.io.*;
public class Main {
 public static void main(String[] args){
  if(args.length==0||args[0].equals("--help")){System.out.println("FIM: init|check|watch [directory] [database] [log]");return;}
  try{run(args);}catch(InterruptedException e){Thread.currentThread().interrupt();}catch(Exception e){System.err.println("FIM error: "+e.getMessage());System.exit(1);}
 }
 public static void run(String[] args)throws Exception{
  String mode=args[0];if(!Set.of("init","check","watch").contains(mode)||args.length>4)throw new IllegalArgumentException("Invalid command");
  Properties p=new Properties();try(InputStream in=Main.class.getResourceAsStream("/application.properties")){if(in!=null)p.load(in);}
  Path root=Path.of(args.length>1?args[1]:p.getProperty("monitor.directory","./watched")).toRealPath();
  Path db=safeOutput(Path.of(args.length>2?args[2]:p.getProperty("database.path","./data/fim.db")),root);
  Path log=safeOutput(Path.of(args.length>3?args[3]:p.getProperty("log.path","./logs/events.log")),root);
  if(db.equals(log))throw new IllegalArgumentException("Database and log must differ");
  var scanner=new DirectoryScanner(new HashService());var manager=new DatabaseManager(db.toString());manager.initializeDatabase();
  var repo=new FileRepository(manager);
  if(mode.equals("init")){var records=scanner.scan(root);repo.initializeBaseline(root.toString(),records);System.out.println("Baseline saved: "+records.size()+" files");return;}
  repo.requireRoot(root.toString());var current=scanner.scan(root);
  var logger=new EventLogger(log);var alert=new AlertService();
  java.util.function.Consumer<FileEvent> report=e->{try{repo.saveEvent(e);logger.log(e);alert.sendAlert(e);}catch(Exception x){throw new IllegalStateException("Could not persist event",x);}};
  var changes=new IntegrityService().compareAll(repo.findAll(),current);changes.forEach(report);
  System.out.println("Baseline differences: "+changes.size());
  if(mode.equals("watch"))new DirectoryWatcher().watch(root,current,report);
 }
 static Path safeOutput(Path input,Path root)throws IOException{
  Path path=input.toAbsolutePath().normalize(),a=path;
  while(!Files.exists(a,LinkOption.NOFOLLOW_LINKS))a=a.getParent();
  Path resolved=a.toRealPath().resolve(a.relativize(path)).normalize();
  if(resolved.startsWith(root))throw new IOException("Database/log must be outside monitored directory");
  return resolved;
 }
}
