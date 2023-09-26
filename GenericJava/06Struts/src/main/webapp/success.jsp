<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ page import="org.apache.struts.Globals"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Welcome Page</title>
	</head>
	<body>
		<h1>Login success</h1>
		<p>
			<bean:message key="success.message" bundle="ApplicationBundle"/>: 
			<bean:write  name="oggetto" property="utente" scope="request" />
		</p>
		<p>
			<c:set var="salario" scope="page" value="2200" />
			<c:if test = "${salario > 2000}">
			 <p>L'importo di <c:out value = "${salario}"/> supera i 2000</p>
			</c:if>${salario}
		</p>
		<p>
			<c:set var = "balance" value = "120000.2309" />
			<p>senza parametri: <fmt:formatNumber value = "${balance}" type = "currency"/></p>
			<p>con il massimo di interi : <fmt:formatNumber type = "number" maxIntegerDigits = "3" value = "${balance}" /></p>
			<p>con il massimo di decimali: <fmt:formatNumber type = "number" maxFractionDigits = "3" value = "${balance}" /></p>
			<p>visualizzato come percentuale:  <fmt:formatNumber type = "percent" maxIntegerDigits="3" value = "${balance}" /></p>
			<p>percentuale con minimo di decimali: <fmt:formatNumber type = "percent" minFractionDigits = "10" value = "${balance}" /><p>
			<p>percentuale con il massimo di interi: <fmt:formatNumber type = "percent" maxIntegerDigits = "3" value = "${balance}" /></p>
			<p>esadecimale: <fmt:formatNumber type = "number" pattern = "###.###E0" value = "${balance}" /></p>
			<p>Con un locale particolare, per esempio in usa si usa il punto come separtore dei decimali mentre in Italia è la virgola
			     <fmt:setLocale value = "en_US"/>
			     <fmt:formatNumber value = "${balance}" type = "currency"/>
			</p>
			<fmt:parseNumber var = "variabile" type = "number" value = "${balance}" />
			<p>Parse senza parametri: </p>
			<fmt:parseNumber var = "variabile" integerOnly = "true" type = "number" value = "${balance}" />
			<p>Parse solo come integer: </p>

			<c:set var = "now" value = "20-10-2010" />
			<fmt:parseDate value = "${now}" var = "parsedEmpDate" pattern = "dd-MM-yyyy" />
			<p>Parsed Date: </p>
			<c:set var = "now" value = "<%= new java.util.Date()%>" />
			 <p>Visualizza l'ora: <fmt:formatDate type = "time" value = "${now}" /></p>
			<p>Visualizza la data (il mese in lettere): < fmt:formatDate type = "date" value = "${now}" /></p>
			<p>Mostra data e ora (il mese in lettere): < fmt:formatDate type = "both" value = "${now}" /></p>
			<p>Mostra data e ora (il mese in numerI): < fmt:formatDate type = "both" dateStyle = "short" timeStyle = "short" value = "${now}" /></p>
			<p>Mostra data e ora (il mese con il nome intero) < fmt:formatDate type = "both" dateStyle = "long" timeStyle = "long" value = "${now}" /></p>
			<p>MOstra la data in formato personalizzato con un pattern specifico: <fmt:formatDate pattern = "yyyy-MM-dd" value = "${now}" /></p>
		</p>	
<p>
Server Version: <%= application.getServerInfo() %><br>
Servlet Version: <%= application.getMajorVersion() %>.<%= application.getMinorVersion() %>
JSP Version: <%= JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %> 
</p>

	</body>
</html>
<!--  http://localhost:8080/06Struts/  -->