<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<% 
    // 클라이언트가 전달하는 데이터의 한글이 깨지지 않도록..
    request.setCharacterEncoding("utf-8");

    // 클라이언트가 전달한 데이터를 추출한다.
    String userId = request.getParameter("user_id");
    String userPw = request.getParameter("user_pw");
    String userNickName = request.getParameter("user_nick_name");
    
    //System.out.println(userId);
    //System.out.println(userPw);
    //System.out.println(userNickName);
    
    //데이터 베이스 접속
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    //쿼리문
    String sql = "insert into user_table " 
                 + "(user_id, user_pw, user_autologin, user_nick_name)"
                 + "values (?, ?, 0, ?)";
                 
    //쿼리 실행
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, userId);
    pstmt.setString(2, userPw);
    pstmt.setString(3, userNickName);
    
    pstmt.execute();
    
    conn.close();
%>