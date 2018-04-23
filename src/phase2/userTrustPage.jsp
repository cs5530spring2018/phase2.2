<%@ page language="java" import="phase2.*" %>
<html>
<head>
    <script LANGUAGE="javascript">

        function check_all_fields(form_obj) {
            if (form_obj.user.value == "") {
                alert("Username cannot be blank");
                return false;
            }
            else if (form_obj.user.value.trim == "") {
                alert("Username cannot be blank");
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

<p>Declare a User (un)trustworthy</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="userTrustPage.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=text name="user" length=254 placeholder="Username"><br>
    <input type=radio name="trust" value=1 checked="checked">Trust
    <input type=radio name="trust" value=0>Distrust<br>
    <input type=submit>
</form><br>
<a href="userLandingPage.jsp">back to landing page</a><br>

<%
} else {
    String user = "";
    String trust = "1";
    if (request.getParameter("user") != null)
        user = request.getParameter("user");
    if (request.getParameter("trust") != null) {
        trust = request.getParameter("trust");
    }
    Connector con = new Connector();
    DbUserService uService = new DbUserService();
    DbTrustService tService = new DbTrustService();
    try {
        if (uService.isLoginAvailable(con.stmt, user, "UberUser")) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("User does not exist!  Please verify the username and try again!");
                window.location.href='userTrustPage.jsp';
            </script>
<%
        } else if (user.equals(username)) {
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("Don't (mis)trust yourself!");
                window.location.href='userTrustPage.jsp';
            </script>
<%
        } else {
            tService.createTrust(con.stmt, username, user, Integer.parseInt(trust));
            con.closeConnection();
%>
            <script LANGUAGE="javascript">
                alert("User (mis)trusted!");
                window.location.href='userTrustPage.jsp';
            </script>
<%
    }
} catch (Exception e) {
    con.closeConnection();
%>
<script LANGUAGE="javascript">
    alert("Something went wrong!");
    window.location.href='userTrustPage.jsp';
</script>
<%
        }
    }
%>
</body>
