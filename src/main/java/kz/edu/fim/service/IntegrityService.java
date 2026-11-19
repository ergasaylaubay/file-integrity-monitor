package kz.edu.fim.service;
import kz.edu.fim.model.*; import java.util.*;
public class IntegrityService {
 public Optional<FileEvent> compare(FileRecord a,FileRecord b){
  if(a==null && b==null)throw new IllegalArgumentException("Both states null");
  if(a==null)return Optional.of(new FileEvent(b.getPath(),EventType.CREATED,null,b.getSha256()));
  if(b==null)return Optional.of(new FileEvent(a.getPath(),EventType.DELETED,a.getSha256(),null));
  if(!a.getPath().equals(b.getPath()))throw new IllegalArgumentException("Different paths");
  return a.getSha256().equals(b.getSha256())?Optional.empty():Optional.of(new FileEvent(b.getPath(),EventType.MODIFIED,a.getSha256(),b.getSha256()));
 }
 public List<FileEvent> compareAll(List<FileRecord> before,List<FileRecord> after){
  Map<String,FileRecord>a=new HashMap<>(),b=new HashMap<>();
  before.forEach(r->a.put(r.getPath(),r));after.forEach(r->b.put(r.getPath(),r));
  Set<String> paths=new TreeSet<>(a.keySet());paths.addAll(b.keySet());
  List<FileEvent> out=new ArrayList<>();for(String p:paths)compare(a.get(p),b.get(p)).ifPresent(out::add);return out;
 }
}
