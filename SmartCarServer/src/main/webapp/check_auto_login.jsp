<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    request.setCharacterEncoding("utf-8");

    String str1 = request.getParameter("login_user_idx");
    int login_user_idx = Integer.parseInt(str1);
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "select user_autologin from user_table "
    		+ "where user_idx = ?";
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, login_user_idx);
    
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    
    int user_autologin = rs.getInt("user_autologin");
    
    conn.close();
%>
<%= user_autologin %>