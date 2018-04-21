<%@ page language="java" import="phase2.*" %>
<%@ page import="java.sql.ResultSet" %>
<html>
<head>
    <script LANGUAGE="javascript">
        function check_all_fields(form_obj) {
            if (form_obj.type == null) {
                alert("choose whether to add or delete");
                return false;
            } else {
                if (form_obj.vin.value == "") {
                    alert("vin cannot be blank");
                    return false;
                }
                if (form_obj.type == "add/remove") {
                    if (form_obj.vin.value == "") {
                        alert("vin cannot be blank");
                        return false;
                    }
                    if (form_obj.category.value == null) {
                        alert("choose a category");
                        return false;
                    }
                    if (form_obj.make.value == "") {
                        alert("make cannot be blank");
                        return false;
                    }
                    if (form_obj.model.value == "") {
                        alert("model cannot be blank");
                        return false;
                    }
                    if (form_obj.year.value == "") {
                        alert("year cannot be blank");
                        return false;
                    }
                }
            }
            return true;
        }
    </script>
</head>
<body>

<%
    Connector con = new Connector();
    DbCarService service = new DbCarService();
    ResultSet rs = service.fetchUberCarsForDriver(con.stmt, (String) request.getSession().getAttribute("username"));
%> <table>
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
        <td><%=rs.getString("vin")%></td>
        <td><%=rs.getString("driver")%></td>
        <td><%=rs.getString("category")%></td>
        <td><%=rs.getString("make")%></td>
        <td><%=rs.getString("model")%></td>
        <td><%=rs.getString("year")%></td>
    </tr>
    <% } %>
</table><%
    String filledFrom = request.getParameter("filledFrom");
    if( filledFrom == null ){
%>

<p>If Removing, you only need to enter the VIN and select 'Remove'</p>
<form name="uberCar" method=get onsubmit="return check_all_fields(this)" action="uberCarMenu.jsp">
    <input type=hidden name="filledFrom" value="filled">
    <input type=text name="vin" length=254 placeholder="Vin Number"><br>
    <input type=radio name="category" value="economy"> Economy
    <input type=radio name="category" value="comfort"> Comfort
    <input type=radio name="category" value="luxury"> Luxury<br>
    <input type=text name="make" length=254 placeholder="Make"><br>
    <input type=text name="model" length=254 placeholder="Model"><br>
    <input type=text name="year" length=254 placeholder="Year"><br>
    <input type=radio name="type" value="add/update"> Add/Update
    <input type=radio name="type" value="remove"> Remove<br>
    <input type=submit>
</form>
<a href="driverLandingPage.jsp">Click Here To Go Back</a>

<%
} else {
    String vin = request.getParameter("vin");
    String category = request.getParameter("category");
    String make = request.getParameter("make");
    String model = request.getParameter("model");
    String year = request.getParameter("year");
    String type = request.getParameter("type");
    try {
        if (type.equals("remove")) {
            service.removeUberCar(con.stmt, vin);
        } else {
            service.createUberCar(con.stmt, vin, (String) request.getSession().getAttribute("username"), category, make, model, Integer.parseInt(year));
        }
        con.closeConnection();
        response.sendRedirect("http://georgia.eng.utah.edu:8080/~5530u61/uberCarMenu.jsp");
    } catch (Exception e) {
        con.closeConnection();
%><script LANGUAGE="javascript">
    alert("something went horribly wrong!");
    window.location.href='uberCarMenu.jsp';
</script> <%
        }
    }
%>
</body>