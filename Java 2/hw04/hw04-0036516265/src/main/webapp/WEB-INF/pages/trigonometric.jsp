<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Sinus values</title>
</head>
<body style="background-color:<%=session.getAttribute("pickedBgCol")%>">

<p>Sinus values are: </p>
<table>
<c:forEach var="entry" items="${valuesMap}">
   <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
</c:forEach>
</table>

</body>
</html>