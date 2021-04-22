<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Background color chooser</title>
</head>
<body style="background-color:<%= session.getAttribute("pickedBgCol") %>">
	<h2>Background color chooser</h2>

	<a href="<% out.print(response.encodeURL(request.getContextPath()+"/setcolor?color=white"));%>">WHITE</a>
	<a href="<% out.print(response.encodeURL(request.getContextPath()+"/setcolor?color=red"));%>">RED</a>	
	<a href="<% out.print(response.encodeURL(request.getContextPath()+"/setcolor?color=green"));%>">GREEN</a>
	<a href="<% out.print(response.encodeURL(request.getContextPath()+"/setcolor?color=cyan"));%>">CYAN</a>
</body>
</html>