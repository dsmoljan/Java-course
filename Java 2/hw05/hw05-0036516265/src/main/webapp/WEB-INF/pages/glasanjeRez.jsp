<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<style type="text/css">
table.rez td {
	text-align: center;
}
</style>
</head>
<body>

	<h1>Rezultati glasanja</h1>
	<p>Ovo su rezultati glasanja.</p>
	<table border="1" cellspacing="0" class="rez">
		<thead>
			<tr>
				<th>Opcija</th>
				<th>Broj glasova</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="option" items="${pollResults}">
				<tr>
					<td>${option.optionTitle}</td>
					<td>${option.votesCount}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<h2>Grafički prikaz rezultata</h2>
	<img alt="Pie-chart" src="<c:url value="/servleti/glasanje-grafika"/>"/>

	<h2>Rezultati u XLS formatu</h2>
	<p>
		Rezultati u XLS formatu dostupni su <a href="<c:url value="/servleti/glasanje-xls"/>">ovdje</a>
	</p>

	<h2>Razno</h2>
	<p>Linkovi na pobjednike:</p>
	<ul>
		<c:forEach var="option" items="${firstOptions}">
			<li><a href="${option.optionLink}">${option.optionTitle}</a></li>
		</c:forEach>
	</ul>
</body>
</html>