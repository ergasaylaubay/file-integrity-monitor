package kz.edu.fim.service;
import kz.edu.fim.model.*; import java.nio.file.*; import java.nio.file.attribute.*; import java.io.*; import java.util.*; import java.util.concurrent.TimeUnit; import java.util.function.Consumer;
public class DirectoryWatcher {
 public void watch(Path root,Consumer<FileEvent> callback)throws IOException,InterruptedException{
  watch(root,new DirectoryScanner(new HashService()).scan(root),callback);
 }
 public void watch(Path directory,List<FileRecord> initial,Consumer<FileEvent> callback)throws IOException,InterruptedException{
  Path root=directory.toAbsolutePath().normalize();
  DirectoryScanner scanner=new DirectoryScanner(new HashService());IntegrityService integrity=new IntegrityService();
  List<FileRecord> previous=initial;
  try(WatchService ws=root.getFileSystem().newWatchService()){
   Map<WatchKey,Path> keys=new HashMap<>();register(root,ws,keys);
   System.out.println("Watching: "+root+" (Ctrl+C to stop)");
   while(!Thread.currentThread().isInterrupted()){
    WatchKey k=ws.poll(1,TimeUnit.SECONDS);
    if(k!=null){Thread.sleep(300);do{k.pollEvents();if(!k.reset())keys.remove(k);k=ws.poll();}while(k!=null);}
    // Full reconciliation also handles overflow and directories created between registrations.
    try{
     register(root,ws,keys);var current=scanner.scan(root);
     for(FileEvent e:integrity.compareAll(previous,current))callback.accept(e);
     previous=current; // Only live snapshot changes; trusted baseline stays untouched.
    }catch(IOException e){System.err.println("Incomplete scan; snapshot retained: "+e.getMessage());}
   }
  }
 }
 private void register(Path root,WatchService ws,Map<WatchKey,Path> keys)throws IOException{
  Set<Path> known=new HashSet<>(keys.values());
  Files.walkFileTree(root,new SimpleFileVisitor<Path>(){
   public FileVisitResult preVisitDirectory(Path p,BasicFileAttributes a)throws IOException{
    if(!known.contains(p))keys.put(p.register(ws,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE),p);
    return FileVisitResult.CONTINUE;
   }
  });
 }
}
