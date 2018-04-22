<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Driver Landing Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
<% String username = (String)request.getSession().getAttribute("username"); %>

<p>Welcome, <%=username%>!</p>
<a href="currentAvailableCarsPage.jsp">find a ride now</a><br>
<a href="reserveCarsPage.jsp">reserve a ride for later</a><br>
<a href="browseCarsPage.jsp">browse cars</a><br>
<a href="favoriteCarsPage.jsp">declare a car as your favorite</a><br>
<a href="reviewsMenuPage.jsp">open reviews menu</a><br>
<a href="userTrustPage.jsp">declare other users as (un)trustworthy</a><br>
<a href="login.jsp">logout</a>
</body>
</html>