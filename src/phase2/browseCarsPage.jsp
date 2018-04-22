<%@ page language="java" import="phase2.*" %>
<%@ page import="java.sql.ResultSet" %>
<html>
<head>

</head>
<body>

<%
    ResultSet rs = null;
    String filledLoginFrom = request.getParameter("filledLoginFrom");
    if( filledLoginFrom == null ){
%>

<p>Filters</p>
<form name="carfilter" method=get  action="browseCarsPage.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=radio name="filter" id ="economyFilter" value="economy">economy<br>
    <input type=radio name="filter" id="comfortFilter" value="comfort">comfort<br>
    <input type=radio name="filter" id="luxuryFilter" value="luxury">luxury<br>
    <input type=radio name="filter" id="noFilter" value="">none<br>
    <br>
    <input type=radio name="andor1" id="and1" value="AND">and
    <input type=radio name="andor1" id="or1" value="OR">or
    <input type=radio name="andor1" id="noAndOr1" value="">none<br>
    <br>
    <input type=text name="model" length=254 placeholder="model"><br>
    <br>
    <input type=radio name="andor2" id="and2" value="AND">and
    <input type=radio name="andor2" id="or2" value="OR">or
    <input type=radio name="andor2" id="noAndOr2" value="">none<br>
    <br>
    <input type=text name="location" length=254 placeholder="city or state"><br>
    <br>
    <input type=radio name="sort" id="sort1" value="a">sort by average score of reviews<br>
    <input type=radio name="sort" id="sort2" value="b">sort by average score of reviews left by trusted users<br>
    <input type=radio name="sort" id="noSort" value="">none<br>
    <br>
    <input type=submit>
</form>

<%
} else {
    String model = request.getParameter("model");
    String location = request.getParameter("location");
    String category = "";
    String andor1 = "";
    String andor2 = "";
    String sort = "";

    if (request.getParameter("andor1") != null)
        andor1 = request.getParameter("andor1");
    if (request.getParameter("andor2") != null)
        andor2 = request.getParameter("andor2");


    if (request.getParameter("filter") != null)
        category = request.getParameter("filter");

    if (category.equals(""))
        andor1 = "";
    if (model == null || model.equals("model"))
        model = "";
    if (location == null || location.equals("city or state"))
        location = "";
    if (request.getParameter("sort") != null)
        sort = request.getParameter("sort");

    Connector con = new Connector();
    DbCarService service = new DbCarService();
    rs = service.ucBrowser(con.stmt, category, andor1, model, andor2, location, sort);

    if (sort.equals("")) {
%>
<a href="userLandingPage.jsp">home</a><br>
<a href="browseCarsPage.jsp">do another search</a><br>
<table cellpadding="10">
    <tr>
        <th>VIN</th>
        <th>DRIVER</th>
        <th>CATEGORY</th>
        <th>MAKE</th>
        <th>MODEL</th>
        <th>YEAR</th>
    </tr>
    <%
        if (rs.isBeforeFirst()) {
            while (rs.next()) { %>
    <tr>
        <td><%=rs.getString(1)%></td>
        <td><%=rs.getString(2)%></td>
        <td><%=rs.getString(3)%></td>
        <td><%=rs.getString(4)%></td>
        <td><%=rs.getString(5)%></td>
        <td><%=rs.getString(6)%></td>
    <%
            }
        }
    }
    else {
        %>
        <a href="userLandingPage.jsp">home</a><br>
        <a href="browseCarsPage.jsp">do another search</a><br>
        <table cellpadding="10">
        <tr>
            <th>VIN</th>
            <th>Average</th>
            <th>DRIVER</th>
            <th>CATEGORY</th>
            <th>MAKE</th>
            <th>MODEL</th>
            <th>YEAR</th>
        </tr>
        <%
            if (rs.isBeforeFirst()) {
                while (rs.next()) { %>
        <tr>
            <td><%=rs.getString(1)%></td>
            <td><%=rs.getString(2)%></td>
            <td><%=rs.getString(3)%></td>
            <td><%=rs.getString(4)%></td>
            <td><%=rs.getString(5)%></td>
            <td><%=rs.getString(6)%></td>
            <td><%=rs.getString(7)%></td>
        </tr>
    <%
            }
        }
    %>
    </table>
<%
        }
        con.closeConnection();
        }
    %>
</body>
