<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    request.setCharacterEncoding("utf-8");

    String userId = request.getParameter("user_id");
    String userPw = request.getParameter("user_pw");
    String str1 = request.getParameter("user_autologin");
    int user_autologin = Integer.parseInt(str1);
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "select user_idx from user_table "
    		+ "where user_id = ? and user_pw = ?";
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, userId);
    pstmt.setString(2, userPw);
    
    ResultSet rs = pstmt.executeQuery();
    boolean chk = rs.next();
    
    if(chk == false){
    	out.write("FAIL");
    }else{
    	int user_idx = rs.getInt("user_idx");
    	
    	String sql2 = "update user_table "
    			    + "set user_autologin = ? "
    			    + "where user_idx = ?";
    	
    	PreparedStatement pstmt2 = conn.prepareStatement(sql2);
    	pstmt2.setInt(1, user_autologin);
    	pstmt2.setInt(2, user_idx);
    	pstmt2.execute();
    	
    	out.write(user_idx + "");
    }
    
    conn.close();
%>