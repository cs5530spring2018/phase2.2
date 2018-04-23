<%@ page language="java" import="phase2.*" %>
<%@ page import="java.sql.ResultSet" %>
<html>
<head>
    <script LANGUAGE="javascript">

        function check_all_fields(form_obj) {
            if (form_obj.vin.value == "") {
                alert("VIN cannot be blank");
                return false;
            }
            else if (form_obj.vin.value.trim == "") {
                alert("VIN cannot be blank");
                return false;
            }
            return true;
        }

    </script>
</head>
<body>

<%
    String username = (String)request.getSession().getAttribute("username");
    String filledLoginFrom = request.getParameter("filledLoginFrom");
    if( filledLoginFrom == null ){
%>

<p>View a Car's Reviews</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="viewCarReviews.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=text name="vin" length=254 placeholder="VIN #"><br>
    <input type=submit>
</form><br>
<a href="reviewsMenuPage.jsp">back to review page</a><br>
<a href="userLandingPage.jsp">back to landing page</a><br>
<a href="browseCarsPage.jsp">browse cars</a>

<%
} else {
    String vin = "";
    if (request.getParameter("vin") != null)
        vin = request.getParameter("vin");

    Connector con = new Connector();
    try {
        DbCarService carService = new DbCarService();
        DbCarFeedbackService fbService = new DbCarFeedbackService();

        if (!carService.uberCarExists(con.stmt, vin)) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Car does not exist!  Please verify the VIN number and try again!");
                window.location.href='favoriteCarsPage.jsp';
            </script>
<%
        } else {

        ResultSet rs = fbService.fetchFeedbackForCar(con.stmt, vin);
%>
<a href="reviewsMenuPage.jsp">back to review page</a><br>
<a href="userLandingPage.jsp">home</a><br>
<a href="viewCarReviews.jsp">do another search</a><br>
<p>If no reviews are listed, the car has no reviews.</p>
<table>
    <tr>
        <th>Reviewer</th>
        <th>VIN</th>
        <th>RATING</th>
        <th>COMMENT</th>
        <th>DATE</th>
    </tr>
    <% while (rs.next()) { %>
    <tr>
        <td><%=rs.getString(1)%>
        </td>
        <td><%=rs.getString(2)%>
        </td>
        <td><%=rs.getString(3)%>
        </td>
        <td><%=rs.getString(4)%>
        </td>
        <td><%=rs.getString(5)%>
        </td>
    </tr>
    <% }
    }
    } catch (Exception e) {
        con.closeConnection();
    %>
    <script LANGUAGE="javascript">
        alert("something went horribly wrong!");
        window.location.href = 'viewCarReviews.jsp';
    </script>
    <%
        }%>
</table>
<%
        con.closeConnection();
    }
%>
</body>
