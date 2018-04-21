<%@ page language="java" import="phase2.*" %>
<%@ page import="java.sql.ResultSet" %>
<html>
<head>
    <script LANGUAGE="javascript">
        function check_all_fields(form_obj) {
            if (form_obj.type == null) {
                alert("choose whether to add or delete");
                return false;
            }
            if (form_obj.day.value == null) {
                alert("choose a day");
                return false;
            }
            if (form_obj.startHour.value == "") {
                alert("starting hour cannot be blank");
                return false;
            }
            if (form_obj.startMin.value == "") {
                alert("starting minutes cannot be blank");
                return false;
            }
            if (form_obj.finishHour.value == "") {
                alert("finish hour cannot be blank");
                return false;
            }
            if (form_obj.finishMin.value == "") {
                alert("finish minutes be blank");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>

<%
    Connector con = new Connector();
    DbHoursOfOpService service = new DbHoursOfOpService();
    ResultSet rs = service.fetchHoursOfOp(con.stmt, (String) request.getSession().getAttribute("username"));
%>
<table>
    <tr>
        <th>DRIVER</th>
        <th>START</th>
        <th>FINISH</th>
        <th>DAY</th>
    </tr>
    <% while (rs.next()) { %>
    <tr>
        <td><%=rs.getString("driver")%>
        </td>
        <td><%=rs.getString("start")%>
        </td>
        <td><%=rs.getString("finish")%>
        </td>
        <td><%=rs.getString("day")%>
        </td>
    </tr>
    <% } %>
</table>
<%
    String filledFrom = request.getParameter("filledFrom");
    if (filledFrom == null) {
%>
<p>Add or Remove Hours of Operation</p>
<form name="hoursOfOp" method=get onsubmit="return check_all_fields(this)" action="hoursOfOpMenu.jsp">
    <input type=hidden name="filledFrom" value="filled">
    <input type=radio name="day" value="0"> Sunday
    <input type=radio name="day" value="1"> Monday
    <input type=radio name="day" value="2"> Tuesday
    <input type=radio name="day" value="3"> Wednesday
    <input type=radio name="day" value="4"> Thursday
    <input type=radio name="day" value="5"> Friday
    <input type=radio name="day" value="6"> Saturday<br>
    <input type=text name="startHour" length=254 placeholder="Starting Hour (0-23)"><br>
    <input type=text name="startMin" length=254 placeholder="Starting Minutes (0-59)"><br>
    <input type=text name="finishHour" length=254 placeholder="Finishing Hour (0-23)"><br>
    <input type=text name="finishMin" length=254 placeholder="Finishing Minutes (0-59)"><br>
    <input type=radio name="type" value="add/update"> Add/Update
    <input type=radio name="type" value="remove"> Remove<br>
    <input type=submit>
</form>
<a href="driverLandingPage.jsp">Click Here To Go Back</a>

<%
} else {
    String day = request.getParameter("day");
    String startHour = request.getParameter("startHour");
    String startMinutes = request.getParameter("startMin");
    String finishHour = request.getParameter("finishHour");
    String finishMinutes = request.getParameter("finishMin");
    String type = request.getParameter("type");
    float convertedStart = Util.convertTime(startHour, startMinutes);
    float convertedFinish = Util.convertTime(finishHour, finishMinutes);
    try {
        if (type.equals("remove")) {
            service.removeHoursOfOp(con.stmt, (String) request.getSession().getAttribute("username"), convertedStart, convertedFinish, Integer.parseInt(day));
        } else {
            service.createHoursOfOp(con.stmt, (String) request.getSession().getAttribute("username"), convertedStart, convertedFinish, Integer.parseInt(day));
        }
        con.closeConnection();
        response.sendRedirect("http://georgia.eng.utah.edu:8080/~5530u61/hoursOfOpMenu.jsp");
    } catch (Exception e) {
        con.closeConnection();
%>
<script LANGUAGE="javascript">
    alert("something went horribly wrong!");
    window.location.href = 'hoursOfOpMenu.jsp';
</script>
<%
        }
    }
%>
</body>
