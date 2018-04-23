<%@ page language="java" import="phase2.*" %>
<%@ page import="java.time.LocalDateTime" %>
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
            if (form_obj.rating.value == "") {
                alert("Rating cannot be blank");
                return false;
            }
            else if (isNaN(form_obj.rating.value) || Number(form_obj.rating.value) < 0 || Number(form_obj.rating.value) > 10) {
                alert("Rating must be a number 0-10");
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

<p>Review a Car</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="leaveCarReviews.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=text name="vin" length=254 placeholder="VIN #"><br>
    <input type=text name="rating" length=254 placeholder="Rating 0-10"><br>
    <input type=text name="comment" length=254 placeholder="Comment (optional)"><br>
    <input type=submit>
</form><br>
<a href="reviewsMenuPage.jsp">back to review page</a><br>
<a href="userLandingPage.jsp">back to landing page</a><br>
<a href="browseCarsPage.jsp">browse cars</a>

<%
} else {
    Connector con = new Connector();
    try {
        String vin = "";
        String rating = "";
        String comment = "";
        if (request.getParameter("vin") != null)
            vin = request.getParameter("vin");
        if (request.getParameter("rating") != null)
            rating = request.getParameter("rating");
        if (request.getParameter("comment") != null)
            comment = request.getParameter("comment");

        DbCarService carService = new DbCarService();
        DbCarFeedbackService fbService = new DbCarFeedbackService();
        if (!carService.uberCarExists(con.stmt, vin)) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Car does not exist!  Please verify the VIN number and try again!");
                window.location.href='leaveCarReviews.jsp';
                </script>
<%
        } else {
            fbService.createCarFeedback(con.stmt, username, vin, Integer.parseInt(rating), comment, LocalDateTime.now());
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Review Created or Updated!");
                window.location.href='leaveCarReviews.jsp';
            </script>
<%
        }
} catch (Exception e) {
    con.closeConnection();
%>
    <script LANGUAGE="javascript">
        alert("Something went wrong!  Please try again!");
        window.location.href='leaveCarReviews.jsp';
    </script>
<%
        }
    }
%>
</body>
