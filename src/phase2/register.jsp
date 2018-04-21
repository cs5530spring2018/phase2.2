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
            if (form_obj.name.value == "") {
                alert("name cannot be blank");
                return false;
            }
            if (form_obj.address.value == "") {
                alert("address cannot be blank");
                return false;
            }
            if (form_obj.phoneNumber.value == "") {
                alert("phone number cannot be blank");
                return false;
            }
            return true;
        }

    </script>
</head>
<body>

<%
    String filledFrom = request.getParameter("filledFrom");
    if( filledFrom == null ){
%>

<p>Registration</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="register.jsp">
    <input type=hidden name="filledFrom" value="filled">
    <input type=text name="username" length=254 placeholder="Username"><br>
    <input type=text name="password" length=254 placeholder="Password"><br>
    <input type=text name="name" length=254 placeholder="First_Name Last_Name"><br>
    <input type=text name="address" length=254 placeholder="Address"><br>
    <input type=text name="phoneNumber" length=12 placeholder="Phone: XXX-XXX-XXXX"><br>
    <input type="checkbox" name="driver" value="isDriver">I am a Driver<br>
    <input type=submit>
</form>

<%
} else {
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String name = request.getParameter("name");
    String address = request.getParameter("address");
    String phoneNumber = request.getParameter("phoneNumber");
    String table = request.getParameter("driver") != null ? "UberDriver" : "UberUser";
    Connector con = new Connector();
    DbUserService service = new DbUserService();
    if(service.isLoginAvailable(con.stmt, username, table)) {
        service.createUser(con.stmt, username, password, name, address, phoneNumber, table);
    } else {
%>
<script>alert("That username has already been taken!");</script>
<%
        }
        con.closeConnection();
    }
%>
</body>