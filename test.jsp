<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>JSP Test Page</title>
</head>
<body>
    <h1>Request forwarded successfully!</h1>
    <p>This is a simple JSP page served as a forward destination.</p>

    <p><strong>Request URI:</strong> <%= request.getRequestURI() %></p>
    <p><strong>Servlet Path:</strong> <%= request.getServletPath() %></p>
    <p><strong>Path Info:</strong> <%= request.getPathInfo() %></p>
</body>
</html>
