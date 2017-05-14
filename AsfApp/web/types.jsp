<%-- 
    Document   : index
    Created on : May 9, 2017, 7:32:18 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Asf App</title>
        <meta name="description" content="Presentation Layer for Asf Project"/>
        <meta name="keywords" content="HTML, CSS, Java, Presentation Layer"/>
        <meta name="author" content="Stefan-Alexandru Rentea"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link rel="stylesheet" type="text/css" href="./CSS/css.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/menu.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/table.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/form.css"/>
    </head>
<body>
    <c:set var="query" scope="session" value="${initialQuery}" />
    <c:set var="index" scope="session" value="${0}" />
    <sql:setDataSource
        var="dataSourceTypes"
        driver="org.apache.derby.jdbc.ClientDriver"
        url="jdbc:derby://localhost:1527/ASF"
        user="root" password="root"
        />
    <sql:query var="rowsTypes" dataSource="${dataSourceTypes}">
        SELECT ID, NAME, DESCRIPTION FROM TYPES
    </sql:query>
    <div class="blacklined">
        <div class="dropdown">
            <a href="home.jsp" class="dropbtn" style="text-decoration: none">HOME</a>
        </div>
        <div class="dropdown">
            <button class="dropbtn">TABLES</button>
            <div class="dropdown-content">
                <a href="types.jsp">TYPES</a>
                <a href="products.jsp">PRODUCTS</a>
            </div>
        </div>
    </div>
    </br></br>
    <form method="get" enctype="text/plain" action="TypesButtons">
        <div align="center" style="overflow-x:auto;">
            <table border="1" cellpadding="5">
                <c:if test="${not empty rowsTypes.rows}">
                    <tr>
                        <th></th>
                        <th>ID</th>
                        <th>NAME</th>
                        <th>DESCRIPTION</th>
                    </tr>
                </c:if>
                <c:forEach var="type" items="${rowsTypes.rows}">
                    <tr>
                        <td><input type="checkbox" name="checkbox_types" value="${type.id}" /></td>
                        <td><c:out value="${type.id}"/></td>
                        <td><c:out value="${type.name}"/></td>
                        <td><c:out value="${type.description}"/></td>
                    </tr>
                </c:forEach>
            </table>
            <br/>
            <table border="1" cellpadding="5">
                <tr>
                    <th>NAME</th>
                    <th>DESCRIPTION</th>
                </tr>
                <tr>
                    <td><input type="text" name="insert_name"/></td>
                    <td><input type="text" name="insert_description"/></td>
                </tr>
            </table>
        </div>
        <input type="submit" value="INSERT" id="submit1" name="insert_button"/>
        <input type="submit" value="UPDATE" id="submit2" name="update_button"/>
        <input type="submit" value="DELETE" id="submit3" name="delete_button"/>
    </form>
    <footer>Copyright &copy; asf.ro</footer>
</body>
</html>
