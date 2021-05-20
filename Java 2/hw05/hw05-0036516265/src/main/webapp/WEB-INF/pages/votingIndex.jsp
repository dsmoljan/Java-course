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
	<h1>${pollTitle}</h1>
	<p>${pollMessage }</p>
	<ol>
		<c:forEach var="option" items="${pollOptions}">
			<li><a href="<c:url value="/servleti/glasanje-glasaj?pollId="/>
				${pollId}&optionId=${option.id}">${option.optionTitle}</a></li>
		</c:forEach>
	</ol>
</body>
</html>