<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    request.setCharacterEncoding("utf-8");

    String str1 = request.getParameter("content_idx");
    int contentIdx = Integer.parseInt(str1);
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "delete from content_table where content_idx = ?";
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, contentIdx);
    
    pstmt.execute();
    
    conn.close();
%>