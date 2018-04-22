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
        function check_all_date_fields(form_obj){
            if (form_obj.year.value == "") {
                alert("year cannot be blank");
                return false;
            }
            if (form_obj.month.value == "") {
                alert("day cannot be blank");
                return false;
            }
            if (form_obj.day.value == "") {
                alert("day cannot be blank");
                return false;
            }
            if (form_obj.hour.value == "") {
                alert("hour cannot be blank");
                return false;
            }
            if (form_obj.minute.value == "") {
                alert("minute cannot be blank");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<%
    String filled = request.getParameter("filled");
    if (filled == null) {
%>
<p>When is your reservation for?</p>
<form name="date" method=get onsubmit="return check_all_date_fields(this)" action="reserveCarsPage.jsp">
    <input type=hidden name="filled" value="filled">
    <input type=text name="year" length=254 placeholder="Year (XXXX)"><br>
    <input type=text name="month" length=254 placeholder="Month (1-12)"><br>
    <input type=text name="day" length=254 placeholder="Day (1-31)"><br>
    <input type=text name="hour" length=254 placeholder="Hour (0-23)"><br>
    <input type=text name="minute" length=254 placeholder="Minute (0-59)"><br>
    <input type=submit>
</form>
<%
    } else {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        String hour = request.getParameter("hour");
        String minute = request.getParameter("minute");
        Connector con = new Connector();
        DbCarService carService = new DbCarService();
        LocalDateTime date = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute));
        request.getSession().setAttribute("date", date);
        ResultSet rs = carService.availableCars(con.stmt, Util.convertTime(Integer.toString(date.getHour()), Integer.toString(date.getMinute())),
            Util.dayOfTheWeekAdjuster(date.getDayOfWeek().getValue()));

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
        rs.close();
        con.closeConnection();
    }%>
</table>
<%

    String filledFrom = request.getParameter("filledFrom");
    if (filledFrom == null) {
%>

<p>Select car from the options above.</p>
<p>If no options are listed, no cars are available. Sorry!</p>
<form name="ride" method=get onsubmit="return check_all_fields(this)" action="reserveCarsPage.jsp">
    <input type=hidden name="filledFrom" value="filled">
    <input type=text name="vin" length=254 placeholder="Vin Number"><br>
    <input type=text name="numRiders" length=254 placeholder="Number Of Riders"><br>
    <input type=text name="distance" length=254 placeholder="Distance In Miles"><br>
    <input type=text name="to" length=254 placeholder="Address You're Going To"><br>
    <input type=text name="from" length=254 placeholder="Address To Be Picked Up From"><br>
    <input type=submit>
</form>
<a href="userLandingPage.jsp">Click Here To Go Back</a><br>
<a href="reserveCarsPage.jsp">Choose A Different Date</a>
<%
    } else {
    String vin = request.getParameter("vin");
    String numRiders = request.getParameter("numRiders");
    String distance = request.getParameter("distance");
    String to = request.getParameter("to");
    String from = request.getParameter("from");
    try {
        Connector con = new Connector();
        DbReservationService reservationService = new DbReservationService();
        LocalDateTime date2 = (LocalDateTime) request.getSession().getAttribute("date");
        if(date2 != null){
            reservationService.createReservation(con.stmt, (String) request.getSession().getAttribute("username"), vin, date2);
        } else {
            %><script LANGUAGE="javascript">
            alert("you must select a date");
            window.location.href = 'reserveCarsPage.jsp';
            </script> <%
        }
        DbCarService carService = new DbCarService();
        ResultSet rs = carService.recommendedCars(con.stmt, vin);
%>
<p>Enter a new date to make another reservation</p>
<p>Suggested Cars Below</p>
<table>
    <tr>
        <th>RIDE_COUNT</th>
        <th>VIN</th>
    </tr>
    <%
        while (rs.next()) { %>
    <tr>
        <td><%=rs.getString(1)%></td>
        <td><%=rs.getString(2)%></td>
    </tr>
    <% }
        rs.close();
        con.closeConnection();
    %>
</table>
<a href="userLandingPage.jsp">Go Back</a>

<script LANGUAGE="javascript">
    alert("reservation successful Here are some suggested cars");
</script>
<%
    } catch (Exception e) {
%>
<script LANGUAGE="javascript">
    alert("something went horribly wrong!");
    window.location.href = 'currentAvailableCarsPage.jsp';
</script>
<%
        }
    }%>
</table>
</body>