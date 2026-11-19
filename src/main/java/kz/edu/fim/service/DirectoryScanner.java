package kz.edu.fim.service;
import kz.edu.fim.model.FileRecord; import java.io.*; import java.nio.file.*; import java.nio.file.attribute.*; import java.util.*;
public class DirectoryScanner {
 private final HashService hashService;
 public DirectoryScanner(HashService h){hashService=h;}
 public List<FileRecord> scan(Path directory) throws IOException {
  Path root=directory.toAbsolutePath().normalize();
  if(!Files.isDirectory(root,LinkOption.NOFOLLOW_LINKS))throw new IOException("Invalid directory: "+root);
  List<FileRecord> out=new ArrayList<>();
  Files.walkFileTree(root,new SimpleFileVisitor<Path>(){
   public FileVisitResult visitFile(Path p,BasicFileAttributes a)throws IOException {
    if(a.isRegularFile())out.add(readStable(p)); return FileVisitResult.CONTINUE;
   }
  }); out.sort(Comparator.comparing(FileRecord::getPath)); return out;
 }
 private FileRecord readStable(Path p)throws IOException {
  for(int i=0;i<3;i++){
   var a=Files.readAttributes(p,BasicFileAttributes.class,LinkOption.NOFOLLOW_LINKS);
   String hash=hashService.calculateHash(p);
   var b=Files.readAttributes(p,BasicFileAttributes.class,LinkOption.NOFOLLOW_LINKS);
   if(a.size()==b.size() && a.lastModifiedTime().equals(b.lastModifiedTime()) && Objects.equals(a.fileKey(),b.fileKey()))
    return new FileRecord(p.toString(),hash,b.size(),b.lastModifiedTime().toMillis());
  } throw new IOException("File changed during read: "+p);
 }
}
