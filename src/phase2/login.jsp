<%@ page language="java" import="phase2.*" %>
<html>
<head>
    <script LANGUAGE="javascript">

        function check_all_fields(form_obj) {
            if (form_obj.username.value == "") {
                alert("username cannot be blank");
                return false;
            }
            if (form_obj.password.value == "") {
                alert("password cannot be blank");
                return false;
            }
            return true;
        }

    </script>
</head>
<body>

<%
    String filledLoginFrom = request.getParameter("filledLoginFrom");
    if( filledLoginFrom == null ){
%>

<p>Log In</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="login.jsp">
    <input type=hidden name="filledLoginFrom" value="filled">
    <input type=text name="username" length=254 placeholder="Username"><br>
    <input type=password name="password" length=254 placeholder="Password"><br>
    <input type="checkbox" name="driver" value="isDriver">I am a Driver<br>
    <input type=submit>
</form><br>
<a href="register.jsp">Don't have an account? Click to here to register </a>

<%
} else {
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String table = request.getParameter("driver") != null ? "UberDriver" : "UberUser";
    Connector con = new Connector();
    DbUserService service = new DbUserService();
    if (service.attemptToLogIn(con.stmt, username, password, table)) {
        request.getSession().setAttribute("username", username);
        //TODO: redirect to landing page
        if(table.equals("UberDriver")) {
            response.sendRedirect("http://georgia.eng.utah.edu:8080/~5530u61/driverLandingPage.jsp");
        } else {
            response.sendRedirect("http://georgia.eng.utah.edu:8080/~5530u61/userLandingPage.jsp");
        }
    } else {
        con.closeConnection();
%>
<script LANGUAGE="javascript">
    alert("Login failed! Check username/password, and make sure to check the box if you are a driver!");
    window.location.href='login.jsp';
</script>
<%
        }
        con.closeConnection();
    }
%>
</body>
