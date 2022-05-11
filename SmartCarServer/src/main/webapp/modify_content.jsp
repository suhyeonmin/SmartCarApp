<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.oreilly.servlet.*" %>
<%@ page import="com.oreilly.servlet.multipart.*" %>
<%
    request.setCharacterEncoding("utf-8");

    String uploadPath = application.getRealPath("upload");
    
    DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
    
    MultipartRequest multi = new MultipartRequest(request, uploadPath, 100*1024*1024, policy);
    
    String str1 = multi.getParameter("content_idx");
    int contentIdx = Integer.parseInt(str1);
    
    String contentSubject = multi.getParameter("content_subject");
    String contentText = multi.getParameter("content_text");
    
    String contentImage = multi.getFilesystemName("content_image");
    
    String str2 = multi.getParameter("content_board_idx");
    int contentBoardIdx = Integer.parseInt(str2);
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw = "2323";
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    if(contentImage == null){
    	String sql = "update content_table "
    			+ "set content_subject = ?, content_text = ?, content_board_idx = ? "
    			+ "where content_idx = ?";
    	PreparedStatement pstmt = conn.prepareStatement(sql);
    	
    	pstmt.setString(1, contentSubject);
    	pstmt.setString(2, contentText);
    	pstmt.setInt(3, contentBoardIdx);
    	pstmt.setInt(4, contentIdx);
    	
    	pstmt.execute();
    } else {
    	String sql = "update content_table "
    			+ "set content_subject = ?, content_text = ?, content_board_idx = ?, content_image = ? "
    			+ "where content_idx = ?";
    	PreparedStatement pstmt = conn.prepareStatement(sql);
    	
    	pstmt.setString(1, contentSubject);
    	pstmt.setString(2, contentText);
    	pstmt.setInt(3, contentBoardIdx);
    	pstmt.setString(4, contentImage);
    	pstmt.setInt(5, contentIdx);
    	
    	pstmt.execute();
    }
    
    conn.close();
%>