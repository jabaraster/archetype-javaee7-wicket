<%@page import="java.util.TimeZone"%>
<%@page import="java.util.Locale"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<h2>Locale: <%=Locale.getDefault() %></h2>
<h2>TimeZone: <%=TimeZone.getDefault() %></h2>
<h2>Date: <%=new java.util.Date() %></h2>

</body>
</html>