package kz.edu.fim.repository;
import kz.edu.fim.model.*; import java.sql.*; import java.util.*;
public class FileRepository {
 private final DatabaseManager databaseManager;
 private static final String INSERT="INSERT INTO baseline_files(path,sha256,file_size,last_modified) VALUES(?,?,?,?)";
 public FileRepository(DatabaseManager d){databaseManager=d;}
 private void bind(PreparedStatement s,FileRecord r)throws SQLException{s.setString(1,r.getPath());s.setString(2,r.getSha256());s.setLong(3,r.getSize());s.setLong(4,r.getLastModified());}
 public void save(FileRecord r)throws SQLException{
  try(Connection c=databaseManager.getConnection();PreparedStatement s=c.prepareStatement(INSERT)){bind(s,r);s.executeUpdate();}
 }
 public void initializeBaseline(String root,List<FileRecord> records)throws SQLException{
  try(Connection c=databaseManager.getConnection()){
   c.setAutoCommit(false);
   try{
    try(Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM fim_metadata")){
     if(rs.next()&&rs.getInt(1)>0)throw new SQLException("Baseline exists. Use a new database for a reviewed baseline.");
    }
    try(PreparedStatement s=c.prepareStatement(INSERT)){for(FileRecord r:records){bind(s,r);s.addBatch();}s.executeBatch();}
    try(PreparedStatement s=c.prepareStatement("INSERT INTO fim_metadata(name,value) VALUES('root',?)")){s.setString(1,root);s.executeUpdate();}
    c.commit();
   }catch(SQLException e){c.rollback();throw e;}
  }
 }
 public void requireRoot(String root)throws SQLException{
  try(Connection c=databaseManager.getConnection();Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT value FROM fim_metadata WHERE name='root'")){
   if(!rs.next())throw new SQLException("Run init first");
   if(!root.equals(rs.getString(1)))throw new SQLException("Baseline belongs to another directory");
  }
 }
 private FileRecord read(ResultSet r)throws SQLException{return new FileRecord(r.getString("path"),r.getString("sha256"),r.getLong("file_size"),r.getLong("last_modified"));}
 public Optional<FileRecord> findByPath(String path)throws SQLException{
  try(Connection c=databaseManager.getConnection();PreparedStatement s=c.prepareStatement("SELECT * FROM baseline_files WHERE path=?")){
   s.setString(1,path);try(ResultSet r=s.executeQuery()){return r.next()?Optional.of(read(r)):Optional.empty();}
  }
 }
 public List<FileRecord> findAll()throws SQLException{
  List<FileRecord> out=new ArrayList<>();try(Connection c=databaseManager.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM baseline_files ORDER BY path")){while(r.next())out.add(read(r));}return out;
 }
 public void saveEvent(FileEvent e)throws SQLException{
  try(Connection c=databaseManager.getConnection();PreparedStatement s=c.prepareStatement("INSERT INTO fim_events(file_path,event_type,old_hash,new_hash,detected_at) VALUES(?,?,?,?,?)")){
   s.setString(1,e.getFilePath());s.setString(2,e.getEventType().name());s.setString(3,e.getOldHash());s.setString(4,e.getNewHash());s.setString(5,e.getDetectedAt().toString());s.executeUpdate();
  }
 }
}
