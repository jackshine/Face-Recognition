package DBUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import Bean.Teacher;
public class DBHelper {
	
	static Connection conn=null;
	static PreparedStatement pstmt=null;
	static ResultSet rs=null;
	private static Connection getConnection() throws Exception{
		InitialContext cxt=new InitialContext();
		if(cxt==null){
			throw new Exception("no context!");
		}
		DataSource ds=(DataSource)cxt.lookup("java:comp/env/jdbc/Structs2DB");
		if(ds==null){
			throw new Exception("no datasource!");
		}
		return ds.getConnection();
	}
	
	private static PreparedStatement getPreparedStatement(Connection conn,String sql)throws Exception{
		if(conn==null||sql==null||sql.trim().equals("")){
			return null;
		}
		PreparedStatement pstmt=conn.prepareStatement(sql.trim());
		return pstmt;
		
	}
	private static void setPreparedStatementParam(PreparedStatement pstmt,List<Object>paramList)throws Exception{
		
		if(pstmt==null||paramList==null||paramList.isEmpty()){
			return;
		}
		for(int i=0;i<paramList.size();i++){
			if(paramList.get(i) instanceof Integer){
				int paramValue=((Integer)paramList.get(i)).intValue();
				pstmt.setInt(i+1, paramValue);
			}else if((paramList.get(i) instanceof String)){
				pstmt.setString(i+1, (String)paramList.get(i));
			}
		}
		return;
	}
	
	private static ResultSet getResultSet(PreparedStatement pstmt)throws Exception{
		if(pstmt==null){
			return null;
		}
		ResultSet rs=pstmt.executeQuery();
		return rs;
	}
	
	private static void closeConnection(Connection conn)throws Exception{
		if(conn==null){
			return;
		}
		try{
			conn.close();
		}catch(SQLException e){
			throw new Exception(e);
		}
	}
	
	private static void closeStatement(Statement stmt)throws Exception{
		if(stmt==null){
			return;
		}
		try{
			stmt.close();
		}catch(SQLException e){
			throw new Exception(e);
		}
	}
	
	private static void closeResultSet(ResultSet rs)throws Exception{
		if(rs==null){
			return;
		}
		try{
			rs.close();
		}catch(SQLException e){
			throw new Exception(e);
		}
	}
	private static void close() throws Exception{
		closeResultSet(rs);
		closeStatement(pstmt);
		closeConnection(conn);
	}
	public static ResultSet execute(String sql,List<Object>paramList) throws Exception{

		try{
			conn=DBHelper.getConnection();
			pstmt=DBHelper.getPreparedStatement(conn, sql);
			setPreparedStatementParam(pstmt,paramList);
			rs=getResultSet(pstmt);

		}catch(SQLException e){
			throw new Exception(e);
		}	
		return rs;
	}
	//从数据库中查询老师
	
	public static ArrayList<Teacher>getTeacher(int label) throws Exception{
		
		ArrayList<Teacher>teachers=new ArrayList<>();
		String sql="SELECT * from teacher WHERE teacherlabel=?";
		List<Object>paramList=new ArrayList<Object>();
		
		paramList.add(label);
		try{		
			conn=DBHelper.getConnection();
			pstmt=DBHelper.getPreparedStatement(conn, sql);
			setPreparedStatementParam(pstmt,paramList);
			rs=getResultSet(pstmt);
			while(rs.next()){
				teachers.add(new Teacher(rs.getString("teacherid"),
						rs.getString("teachername"), 
						Integer.parseInt(rs.getString("teacherage")), 
						rs.getString("teachergender"), 
						rs.getString("teacherinfo"), 
						Integer.parseInt(rs.getString("teacherlabel"))));
			}
		}catch(SQLException e){
			throw new Exception(e);
		}finally{
			close();
		}	
		return teachers;
	}
}
 