<%@ page language="java" import="phase2.*" %>
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

<p>Favorite a Car</p>
<form name="register" method=get onsubmit="return check_all_fields(this)" action="favoriteCarsPage.jsp">
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
    DbCarService carService = new DbCarService();
    DbFavoritesService fService = new DbFavoritesService();
    try {
        if (!carService.uberCarExists(con.stmt, vin)) {
            con.closeConnection();
            %>
                <script LANGUAGE="javascript">
                    alert("Car does not exist!  Please verify the VIN number and try again!");
                    window.location.href='favoriteCarsPage.jsp';
                </script>
            <%
        } else if (fService.favoriteExists(con.stmt, username, vin)) {
            con.closeConnection();
            %>
                <script LANGUAGE="javascript">
                    alert("Already Favorited!");
                    window.location.href='favoriteCarsPage.jsp';
                </script>
            <%
        } else {
            fService.createFavorite(con.stmt, username, vin);
            con.closeConnection();
            %>
            <script LANGUAGE="javascript">
                alert("Favorited!");
                window.location.href='favoriteCarsPage.jsp';
            </script>
            <%
        }
    } catch (Exception e) {
        con.closeConnection();
        %>
        <script LANGUAGE="javascript">
            alert("Something went wrong!  Please verify the VIN number and try again!");
            window.location.href='favoriteCarsPage.jsp';
        </script>
        <%
    }
    }
%>
</body>
