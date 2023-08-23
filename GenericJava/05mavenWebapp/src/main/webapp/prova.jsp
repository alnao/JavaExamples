<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/WEB-INF/prova.tld" prefix="alnaoTLD" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Prova</title>
    <style>.classeCCS1{color:green;} .classeCCS2{color:red;}</style>
  </head>
  <body> 
 	Pagina di prova ( http://localhost:8081/05mavenWebapp/ )
 	<hr />
 	BasePath = <%=basePath%>
 	<hr />
 	<%=request.getAttribute("nomeInRequest") %>
 	<%=request.getSession().getAttribute("cognomeInSessione") %>
 	<hr />
 	Commento e ora: <alnaoTLD:TagCommentoSenzaParametri />
 	<hr />
	<alnaoTLD:Importo positiveStyle="classeCCS1" negativeStyle="classeCCS2">-12.42</alnaoTLD:Importo>
	<hr />
  </body>
</html>