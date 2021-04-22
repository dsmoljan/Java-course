<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- namjerno je ostavljeno više načina pristupa nekom URL-u -->
<!DOCTYPE html>
<html>
<body
	style="background-color:<%=session.getAttribute("pickedBgCol")%>">
	<h2>Welcome!</h2>
	<a
		href="<%out.print(response.encodeURL(request.getContextPath() + "/pages/colors.jsp"));%>">Background
		color chooser</a>
	<br></br>
	<a
		href="<%out.print(response.encodeURL(request.getContextPath() + "/trigonometric?a=0&b=90"));%>">Calculate
		sinus</a>
	<br></br>
	<form action="trigonometric" method="GET">
		Početni kut:<br>
		<input type="number" name="a" min="0" max="360" step="1" value="0"><br>
		Završni kut:<br>
		<input type="number" name="b" min="0" max="360" step="1" value="360"><br>
		<input type="submit" value="Tabeliraj"><input type="reset"
			value="Reset">
	</form>
	<a 
		href="<c:url value="/funny" />">Funny story</a>
	<br></br>
	<a
		href="<c:url value="/pages/report.jsp" />">OS usage</a>
			<br></br>
	<a
		href="<c:url value="/powers?a=1&b=100&n=3" />">Generate XLS - Powers</a>
	<br></br>
	<a
		href="<c:url value="/appinfo" />">App info</a>
	<br></br>
	<a
		href="<c:url value="/glasanje" />">Glasanje</a>
</body>
</html>
