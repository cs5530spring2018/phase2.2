<%@ page language="java" import="phase2.*" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.sql.ResultSet" %>
<html>
<head>
    <script LANGUAGE="javascript">

        function check_all_fields(form_obj) {
            if (form_obj.driver.value == "") {
                alert("Driver cannot be blank");
                return false;
            }
            if (form_obj.limit.value == "") {
                alert("Limit cannot be blank");
                return false;
            }
            else if (isNaN(form_obj.limit.value) || Number(form_obj.limit.value) < 1) {
                alert("Limit must be a number greater than 0");
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

<p>Driver Review Sorted by Most Useful User Reviews</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="viewUsefulReviews.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=text name="driver" length=254 placeholder="Uber Driver"><br>
    <input type=text name="limit" length=254 placeholder="Review Limit"><br>
    <input type=submit>
</form><br>
<a href="reviewsMenuPage.jsp">back to review page</a><br>
<a href="userLandingPage.jsp">back to landing page</a><br>

<%
} else {
    Connector con = new Connector();
    try {
        String driver = "";
        String limit = "";
        if (request.getParameter("driver") != null)
            driver = request.getParameter("driver");
        if (request.getParameter("limit") != null)
            limit = request.getParameter("limit");

        DbUserService uService = new DbUserService();
        DbScoredFeedbackService fbService = new DbScoredFeedbackService();
        if (uService.isLoginAvailable(con.stmt, driver, "UberDriver")) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Driver does not exist!  Please check the username and try again!");
                window.location.href='viewUsefulReviews.jsp';
            </script>
<%
        } else {
            ResultSet rs = fbService.usefulFeedbackByDriver(con.stmt, driver, Integer.parseInt(limit));
%>
            <a href="reviewsMenuPage.jsp">back to review page</a><br>
            <a href="userLandingPage.jsp">back to landing page</a><br>
            <a href="viewUsefulReviews.jsp">do another search</a><br>
            <p>If no reviews are listed, the car has no reviews.</p>
            <table>
                <tr>
                    <th>REVIEWEE</th>
                    <th>CAR VIN</th>
                    <th>REVIEWER</th>
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
                    <td><%=rs.getString(6)%>
                    </td>
                </tr>
                    <% }

    con.closeConnection();
    }
} catch (Exception e) {
    con.closeConnection();
%>
    <script LANGUAGE="javascript">
        alert("Something went wrong!  Please try again!");
        window.location.href='viewUsefulReviews.jsp';
    </script>
<%
        }
    }
%>
</body>
