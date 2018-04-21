<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Driver Landing Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
    <% String username = (String)request.getSession().getAttribute("username"); %>

<p>Welcome, <%=username%>!</p>
    <p>You are logged in as a Driver!</p>
<a href="">Your UberCar Menu</a><br>
<a href="">Your HoursOfOp Menu</a>
<a href="index.html">Logout</a>
</body>
</html>
