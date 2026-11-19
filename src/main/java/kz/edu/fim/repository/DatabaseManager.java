package kz.edu.fim.repository;
import java.sql.*; import java.nio.file.*; import java.io.*;
public class DatabaseManager {
 private final String databasePath;
 public DatabaseManager(String p){databasePath=p;}
 public Connection getConnection()throws SQLException{
  Path p=Path.of(databasePath).toAbsolutePath().normalize();
  try{Files.createDirectories(p.getParent());}catch(IOException e){throw new SQLException(e);}
  Connection c=DriverManager.getConnection("jdbc:sqlite:"+p);
  try(Statement s=c.createStatement()){s.execute("PRAGMA busy_timeout=5000");}return c;
 }
 public void initializeDatabase()throws SQLException{
  try(Connection c=getConnection();Statement s=c.createStatement()){
   s.executeUpdate("CREATE TABLE IF NOT EXISTS baseline_files(path TEXT PRIMARY KEY,sha256 TEXT NOT NULL,file_size INTEGER NOT NULL,last_modified INTEGER NOT NULL)");
   s.executeUpdate("CREATE TABLE IF NOT EXISTS fim_metadata(name TEXT PRIMARY KEY,value TEXT NOT NULL)");
   s.executeUpdate("CREATE TABLE IF NOT EXISTS fim_events(id INTEGER PRIMARY KEY AUTOINCREMENT,file_path TEXT NOT NULL,event_type TEXT NOT NULL,old_hash TEXT,new_hash TEXT,detected_at TEXT NOT NULL)");
  }
 }
}
