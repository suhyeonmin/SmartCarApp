<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="org.json.simple.*" %>

<%
    request.setCharacterEncoding("utf-8");

    String str1 = request.getParameter("read_content_idx");
    int readContentIdx = Integer.parseInt(str1);
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "select a1.content_subject, a2.user_nick_name as content_nick_name, "
    		+ "date_format(a1.content_write_date, '%Y-%m-%d') as content_write_date, "
    		+ "a1.content_text, a1.content_image, a1.content_writer_idx, a1.content_board_idx "
    	    + "from content_table a1, user_table a2 "
    	    + "where a1.content_writer_idx = a2.user_idx "
    	    + "and content_idx = ?";
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, readContentIdx);
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    
    JSONObject obj = new JSONObject();
    
    String contentSubject = rs.getString("content_subject");
    String contentNickName = rs.getString("content_nick_name");
    String contentWriteDate = rs.getString("content_write_date");
    String contentText = rs.getString("content_text");
    String contentImage = rs.getString("content_image");
    int contentWriterIdx = rs.getInt("content_writer_idx");
    int contentBoardIdx = rs. getInt("content_board_idx");
    
    obj.put("content_subject", contentSubject);
    obj.put("content_nick_name", contentNickName);
    obj.put("content_write_date", contentWriteDate);
    obj.put("content_text", contentText);
    obj.put("content_image", contentImage);
    obj.put("content_writer_idx", contentWriterIdx);
    obj.put("content_board_idx", contentBoardIdx);
    
    conn.close();
%>
<%= obj.toJSONString() %>