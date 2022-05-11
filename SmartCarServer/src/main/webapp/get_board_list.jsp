<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<% 
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "select board_idx, board_name from board_table order by board_idx";
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    
    JSONArray root = new JSONArray();
    
    while(rs.next()){
    	int boardIdx = rs.getInt("board_idx");
    	String boardName = rs.getString("board_name");
    	
    	JSONObject obj = new JSONObject();
    	obj.put("board_idx", boardIdx);
    	obj.put("board_name", boardName);
    	
    	root.add(obj);
    }
    
    conn.close();

%>
<%= root.toJSONString() %>