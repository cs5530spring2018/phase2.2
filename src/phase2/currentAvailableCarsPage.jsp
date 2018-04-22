<%@ page language="java" import="phase2.*" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.time.LocalDateTime" %>
<html>
<head>
    <script LANGUAGE="javascript">
        function check_all_fields(form_obj) {
            if (form_obj.vin.value == "") {
                alert("vin cannot be blank");
                return false;
            }
            if (form_obj.numRiders.value == "") {
                alert("number of riders cannot be blank");
                return false;
            }
            if (form_obj.distance.value == "") {
                alert("distance cannot be blank");
                return false;
            }
            if (form_obj.to.value == "") {
                alert("address to cannot be blank");
                return false;
            }
            if (form_obj.from.value == "") {
                alert("pickup address cannot be blank");
                return false;
            }
            return confirm('Are you sure you want to request a ride from ' + form_obj.vin.value + '?')
        }
    </script>
</head>
<body>

<%
    try {
    Connector con = new Connector();
    DbCarService service = new DbCarService();
    ResultSet rs = service.availableCars(con.stmt, Util.getNowTimeAsFloat(), Util.dayOfTheWeekAdjuster(LocalDateTime.now().getDayOfWeek().getValue()));
%>
<table>
    <tr>
        <th>VIN</th>
        <th>DRIVER</th>
        <th>CATEGORY</th>
        <th>MAKE</th>
        <th>MODEL</th>
        <th>YEAR</th>
    </tr>
    <% while (rs.next()) { %>
    <tr>
        <td><%=rs.getString("vin")%>
        </td>
        <td><%=rs.getString("driver")%>
        </td>
        <td><%=rs.getString("category")%>
        </td>
        <td><%=rs.getString("make")%>
        </td>
        <td><%=rs.getString("model")%>
        </td>
        <td><%=rs.getString("year")%>
        </td>
    </tr>
    <% }
    } catch (Exception e) {
        con.closeConnection();
        %>
        <script LANGUAGE="javascript">
            alert("something went horribly wrong!");
            window.location.href = 'currentAvailableCarsPage.jsp';
        </script>
        <%
    }%>
</table>
<%
    String filledFrom = request.getParameter("filledFrom");
    if (filledFrom == null) {
%>

<p>Select car from the options above.</p>
<p>If no options are listed, no cars are available. Sorry!</p>
<form name="ride" method=get onsubmit="return check_all_fields(this)" action="currentAvailableCarsPage.jsp">
    <input type=hidden name="filledFrom" value="filled">
    <input type=text name="vin" length=254 placeholder="Vin Number"><br>
    <input type=text name="numRiders" length=254 placeholder="Number Of Riders"><br>
    <input type=text name="distance" length=254 placeholder="Distance In Miles"><br>
    <input type=text name="to" length=254 placeholder="Address You're Going To"><br>
    <input type=text name="from" length=254 placeholder="Address To Be Picked Up From"><br>
    <input type=submit>
</form>
<a href="userLandingPage.jsp">Click Here To Go Back</a>

<%
} else {
    String vin = request.getParameter("vin");
    String numRiders = request.getParameter("numRiders");
    String distance = request.getParameter("distance");
    String to = request.getParameter("to");
    String from = request.getParameter("from");
    try {
        DbRideService rideService = new DbRideService();
        rideService.createRide(con.stmt, (String) request.getSession().getAttribute("username"), vin, Integer.parseInt(numRiders),
                Double.parseDouble(distance), Double.parseDouble(distance), LocalDateTime.now(), to, from);
        con.closeConnection();
%>
<script LANGUAGE="javascript">
    alert("ride successfully requested");
    window.location.href = 'currentAvailableCarsPage.jsp';
</script>
<%
    } catch (Exception e) {
        con.closeConnection();
%>
<script LANGUAGE="javascript">
    alert("something went horribly wrong!");
    window.location.href = 'currentAvailableCarsPage.jsp';
</script>
<%
        }
    }
%>
</body>