package kz.edu.fim;
import org.junit.jupiter.api.*; import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import kz.edu.fim.service.*; import kz.edu.fim.repository.*; import kz.edu.fim.notification.*; import kz.edu.fim.model.*;
import java.nio.file.*; import java.util.*; import java.util.concurrent.*;
class FimTest {
 @TempDir Path temp;
 FileRecord r(String hash){return new FileRecord("a",hash,1,0);}
 @Test void knownHash()throws Exception{
  Path p=Files.writeString(temp.resolve("a"),"abc");
  assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",new HashService().calculateHash(p));
 }
 @Test void emptyHash()throws Exception{
  Path p=Files.createFile(temp.resolve("a"));
  assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",new HashService().calculateHash(p));
 }
 @Test void missingHash(){assertThrows(java.io.IOException.class,()->new HashService().calculateHash(temp.resolve("missing")));}
 @Test void recursiveScan()throws Exception{
  Files.writeString(Files.createDirectories(temp.resolve("sub")).resolve("a"),"x");
  assertEquals(1,new DirectoryScanner(new HashService()).scan(temp).size());
 }
 @Test void missingDirectory(){assertThrows(java.io.IOException.class,()->new DirectoryScanner(new HashService()).scan(temp.resolve("x")));}
 @Test void created(){assertEquals(EventType.CREATED,new IntegrityService().compare(null,r("x")).orElseThrow().getEventType());}
 @Test void modified(){assertEquals(EventType.MODIFIED,new IntegrityService().compare(r("x"),r("y")).orElseThrow().getEventType());}
 @Test void deleted(){assertEquals(EventType.DELETED,new IntegrityService().compare(r("x"),null).orElseThrow().getEventType());}
 @Test void same(){assertTrue(new IntegrityService().compare(r("x"),r("x")).isEmpty());}
 @Test void invalidStates(){assertThrows(IllegalArgumentException.class,()->new IntegrityService().compare(null,null));}
 @Test void logAppend()throws Exception{
  Path p=temp.resolve("logs/events.log");var l=new EventLogger(p);
  var e=new FileEvent("a",EventType.CREATED,null,"x");l.log(e);l.log(e);
  assertEquals(2,Files.readAllLines(p).size());
 }
 @Test void databaseRoundTripAndBaselineGuard()throws Exception{
  var d=new DatabaseManager(temp.resolve("db/fim.db").toString());d.initializeDatabase();var repo=new FileRepository(d);
  repo.initializeBaseline("root",List.of(r("x")));repo.requireRoot("root");
  assertEquals("x",repo.findByPath("a").orElseThrow().getSha256());
  assertTrue(repo.findByPath("missing").isEmpty());assertEquals(1,repo.findAll().size());
  assertThrows(java.sql.SQLException.class,()->repo.initializeBaseline("root",List.of(r("y"))));
  assertThrows(java.sql.SQLException.class,()->repo.requireRoot("other"));
  repo.saveEvent(new FileEvent("a",EventType.MODIFIED,"x","y"));
  try(var c=d.getConnection();var s=c.createStatement();var rs=s.executeQuery("SELECT COUNT(*) FROM fim_events")){
   assertTrue(rs.next());assertEquals(1,rs.getInt(1));
  }
 }
 @Test void rejectsOutputInsideRoot()throws Exception{
  assertThrows(java.io.IOException.class,()->Main.safeOutput(temp.resolve("db/fim.db"),temp.toRealPath()));
 }
 @Test void cliDoesNotReplaceBaseline()throws Exception{
  Path root=Files.createDirectory(temp.resolve("watched"));Path f=Files.writeString(root.resolve("a"),"old");
  String db=temp.resolve("db/fim.db").toString(),log=temp.resolve("logs/events.log").toString();
  Main.run(new String[]{"init",root.toString(),db,log});Files.writeString(f,"new");
  Main.run(new String[]{"check",root.toString(),db,log});
  var repo=new FileRepository(new DatabaseManager(db));
  assertNotEquals(new HashService().calculateHash(f),repo.findByPath(f.toRealPath().toString()).orElseThrow().getSha256());
  assertTrue(Files.readString(Path.of(log)).contains("MODIFIED"));
 }
 @Test void watcherCreateModifyDeleteAndStop()throws Exception{
  Path root=Files.createDirectory(temp.resolve("watched"));
  BlockingQueue<FileEvent> events=new LinkedBlockingQueue<>();
  ExecutorService executor=Executors.newSingleThreadExecutor();
  Future<?> job=executor.submit(()->{try{new DirectoryWatcher().watch(root,List.of(),events::add);}catch(InterruptedException e){Thread.currentThread().interrupt();}catch(Exception e){throw new RuntimeException(e);}});
  try{
   Path sub=Files.createDirectories(root.resolve("nested"));
   Path f=Files.writeString(sub.resolve("a"),"one");
   FileEvent e=events.poll(8,TimeUnit.SECONDS);assertNotNull(e);assertEquals(EventType.CREATED,e.getEventType());
   Files.writeString(f,"two");e=events.poll(8,TimeUnit.SECONDS);assertNotNull(e);assertEquals(EventType.MODIFIED,e.getEventType());
   Files.delete(f);e=events.poll(8,TimeUnit.SECONDS);assertNotNull(e);assertEquals(EventType.DELETED,e.getEventType());
  }finally{job.cancel(true);executor.shutdownNow();assertTrue(executor.awaitTermination(5,TimeUnit.SECONDS));}
 }
}
