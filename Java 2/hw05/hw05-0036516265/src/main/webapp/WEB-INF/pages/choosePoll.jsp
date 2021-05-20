<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Odabir glasanja</title>
</head>
<body>
	<h1>Odabir ankete</h1>
	<p>Odaberite neku od ponuđenih anketa kako biste glasali</p>
	<ol>
		<c:forEach var="poll" items="${pollList}">
			<li><a href="<c:url value="/servleti/glasanje?pollID="/>
				${poll.id}">${poll.title}</a></li>
		</c:forEach>
	</ol>
</body>
</html>