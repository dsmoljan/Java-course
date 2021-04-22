<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>OS usage</title>
</head>
<body style="background-color:<%=session.getAttribute("pickedBgCol")%>">
<p>Here are the results of OS usage</p>
<img src="<c:url value="/reportImage"/>" alt="OS usage">
</body>
</html>