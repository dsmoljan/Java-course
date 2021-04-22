<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled bendova</title>
</head>
<body style="background-color:<%=session.getAttribute("pickedBgCol")%>">
	<h1>Glasanje za omiljeni bend</h1>
	<p>Od sljedećih bendova, koji vam je najdraži? Kliknite na link
		kako biste glasali!</p>
	<ol>
		<c:forEach var="band" items="${bandMap}">
			<li><a href="<c:url value="/glasanje-glasaj?id="/>
				${band.value.UID}">${band.value.name}</a></li>
		</c:forEach>
	</ol>
</body>
</html>