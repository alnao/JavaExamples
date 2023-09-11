<%@page import="org.apache.struts.Globals"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Welcome Page</title>
	</head>
	<body>
		<h1>Login success</h1>
		<p><bean:message key="success.message" bundle="ApplicationBundle"/></p>
	</body>
</html>
<!--  http://localhost:8080/06Struts/  -->