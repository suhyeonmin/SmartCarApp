<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.oreilly.servlet.*" %>
<%@ page import="com.oreilly.servlet.multipart.*" %>
<%
    request.setCharacterEncoding("utf-8");

    String uploadPath = application.getRealPath("upload");
     // System.out.println(uploadPath);
     
     // 파일 업로드 처리
     DefaultFileRenamePolicy policy = new DefaultFileRenamePolicy();
     MultipartRequest multi = new MultipartRequest(request, uploadPath, 100 * 1024 * 1024, "utf-8", policy);

    String str1 = multi.getParameter("content_board_idx");
    int content_board_idx = Integer.parseInt(str1);
    
    String str2 = multi.getParameter("content_writer_idx");
    int content_writer_idx = Integer.parseInt(str2);
    
    String content_subject = multi.getParameter("content_subject");
    String content_text = multi.getParameter("content_text");
    
    String content_image = multi.getFilesystemName("content_image");
    
    String dbUrl = "jdbc:mysql://localhost:3306/community_db";
    String dbId = "root";
    String dbPw ="2323";
    Class.forName("com.mysql.cj.jdbc.Driver");
    
    Connection conn = DriverManager.getConnection(dbUrl, dbId, dbPw);
    
    String sql = "insert into content_table "
    		+ "(content_board_idx, content_writer_idx, content_subject, "
    		+ "content_write_date, content_text, content_image) "
    		+ "values (?, ?, ?, now(), ?, ?)";
    /*
    for(int i = 0 ; i < 43 ; i++){
    	PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, content_board_idx);
        pstmt.setInt(2, content_writer_idx);
        pstmt.setString(3, content_subject + i);
        pstmt.setString(4, content_text);
        pstmt.setString(5, content_image);
        
        pstmt.execute();
    }
    */
    
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, content_board_idx);
    pstmt.setInt(2, content_writer_idx);
    pstmt.setString(3, content_subject);
    pstmt.setString(4, content_text);
    pstmt.setString(5, content_image);
    
    pstmt.execute();
    
    
    String sql2 = "select max(content_idx) as read_content_idx from content_table "
    		+ "where content_board_idx = ?";
    
    PreparedStatement pstmt2 = conn.prepareStatement(sql2);
    pstmt2.setInt(1, content_board_idx);
    ResultSet rs = pstmt2.executeQuery();
    rs.next();
    int read_content_idx = rs.getInt("read_content_idx");
    
    conn.close();
%>
<%= read_content_idx %>
