<%@ page language="java" import="phase2.*" %>
<%@ page import="java.time.LocalDateTime" %>
<html>
<head>
    <script LANGUAGE="javascript">

        function check_all_fields(form_obj) {
            if (form_obj.vin.value == null) {
                alert("VIN cannot be blank");
                return false;
            }
            else if (form_obj.vin.value.trim == "") {
                alert("VIN cannot be blank");
                return false;
            }
            if (form_obj.reviewer.value == null) {
                alert("Reviewer cannot be blank");
                return false;
            }
            if (form_obj.score.value == null) {
                alert("Score cannot be blank");
            }
            else if (isNaN(form_obj.score.value) || Number(form_obj.score.value) < 0 || Number(form_obj.score.value) > 2) {
                alert("Rating must be a number 0-2");
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

<p>Score a User Review</p>
<p>0 is 'useless', 1 is 'useful', 2 is 'very useful'</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="login.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=text name="vin" length=254 placeholder="VIN #"><br>
    <input type=text name="reviewer" length=254 placeholder="Reviewer's Username"><br>
    <input type=text name="score" length=1 placeholder="Score 0-2"><br>
    <input type=submit>
</form><br>
<a href="userLandingPage.jsp">back to landing page</a><br>
<a href="viewCarReviews.jsp">browse reviews</a>

<%
} else {
    try {
        String vin = "";
        String reviewer = "";
        String score = "";
        if (request.getParameter("vin") != null)
            vin = request.getParameter("vin");
        if (request.getParameter("reviewer") != null)
            reviewer = request.getParameter("rating");
        if (request.getParameter("score") != null)
            score = request.getParameter("score");
        Connector con = new Connector();
        DbCarService carService = new DbCarService();
        DbScoredFeedbackService fbService = new DbScoredFeedbackService();
        if (!carService.uberCarExists(con.stmt, vin)) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Car does not exist!  Please verify the VIN number and try again!");
                window.location.href='scoreCarReviews.jsp';
            </script>
<%
        } else if (reviewer.equals(username)) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("You can't review yourself!");
                window.location.href='scoreCarReviews.jsp';
            </script>
<%
        } else {
            fbService.createScoredFeedback(con.stmt, reviewer, vin, username, score);
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Review Scored!");
                window.location.href='scoreCarReviews.jsp';
            </script>
<%
        }
} catch (Exception e) {
    con.closeConnection();
%>
<script LANGUAGE="javascript">
    alert("Something went wrong!  Please verify the VIN number and try again!");
    window.location.href='scoreCarReviews.jsp';
</script>
<%
        }
    }
%>
</body>
