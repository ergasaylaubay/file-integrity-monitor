package kz.edu.fim.service;
import java.io.*; import java.nio.file.*; import java.security.*; import java.util.HexFormat;
public class HashService {
 public String calculateHash(Path p) throws IOException {
  if(!Files.isRegularFile(p,LinkOption.NOFOLLOW_LINKS)) throw new IOException("Not a regular file: "+p);
  try { var d=MessageDigest.getInstance("SHA-256");
   try(var in=Files.newInputStream(p,LinkOption.NOFOLLOW_LINKS)){byte[] b=new byte[8192]; int n; while((n=in.read(b))!=-1)d.update(b,0,n);}
   return HexFormat.of().formatHex(d.digest());
  } catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
 }
}
