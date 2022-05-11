<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>
<%
    request.setCharacterEncoding("utf-8");

    String str1 = request.getParameter("content_board_idx");
    int content_board_idx = Integer.parseInt(str1);
    
    String str2 = request.getParameter("page_num");
    int page_num = Integer.parseInt(str2);
    
    int startIndex = (page_num - 1) * 10;
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "select a1.content_subject, a2.user_nick_name as content_nick_name, "
    	       + "date_format(a1.content_write_date, '%Y-%m-%d') as content_write_date, " 
    	       + "a1.content_idx "
    	       + "from content_table a1, user_table a2 "
    	       + "where a1.content_writer_idx = a2.user_idx ";
    	       
    if(content_board_idx != 0){
    	sql += "and a1.content_board_idx = ? ";
    }
    sql += "order by a1.content_idx desc limit ?, 10";
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    if(content_board_idx != 0){
    	pstmt.setInt(1, content_board_idx);
    	pstmt.setInt(2, startIndex);
    } else{
    	pstmt.setInt(1, startIndex);
    }
    
    ResultSet rs = pstmt.executeQuery();
    
    JSONArray root = new JSONArray();
    
    while(rs.next()){
    	int contentIdx = rs.getInt("content_idx");
    	String contentNickName = rs.getString("content_nick_name");
    	String contentWriteDate = rs.getString("content_write_date");
    	String contentSubject = rs.getString("content_subject");
    	
    	JSONObject obj = new JSONObject();
    	obj.put("content_idx", contentIdx);
    	obj.put("content_nick_name", contentNickName);
    	obj.put("content_write_date", contentWriteDate);
    	obj.put("content_subject", contentSubject);
    	
    	root.add(obj);
    }
    
    conn.close();
%>
<%= root.toJSONString() %>