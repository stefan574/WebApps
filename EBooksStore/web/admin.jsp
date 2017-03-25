<%-- 
    Document   : admin
    Created on : Mar 24, 2017, 2:37:28 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Admin Page</title>
        <meta name="description" content="Presentation Layer for EBooks Project"/>
        <meta name="keywords" content="HTML, CSS, Java, Presentation Layer"/>
        <meta name="author" content="Stefan-Alexandru Rentea"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link rel="stylesheet" type="text/css" href="./CSS/css.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/menu.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/table.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/form.css"/>
    </head>
<body>
    <c:choose>
        <c:when test="${validUser == true && roleUser == 'admin'}">
            <sql:setDataSource
                var="dataSourceUsers"
                driver="org.apache.derby.jdbc.ClientDriver"
                url="jdbc:derby://localhost:1527/EBOOKS_DB"
                user="root" password="root"
                />
            <sql:query var="rowsUsers" dataSource="${dataSourceUsers}">
                SELECT USERS.USER_NAME, USERS.USER_ROLE, USERS.PASSWORD, 
                PERMISSIONS.INS, PERMISSIONS.UPD, PERMISSIONS.DEL FROM USERS 
                JOIN PERMISSIONS ON USERS.USER_NAME = PERMISSIONS.USER_NAME
            </sql:query>
            <div class="blacklined">
                <div class="dropdown">
                    <a href="home.jsp" class="dropbtn" style="text-decoration: none">HOME</a>
                </div>
                <c:if test="${roleUser == 'admin'}">
                    <div class="dropdown">
                        <a href="admin.jsp" class="dropbtn" style="text-decoration: none">ADMIN</a>
                    </div>
                </c:if>
                <div class="dropdown">
                    <button class="dropbtn">TABLES</button>
                    <div class="dropdown-content">
                        <a href="ebooks.jsp">EBOOKS</a>
                        <a href="authors.jsp">AUTHORS</a>
                        <a href="ratings.jsp">RATINGS</a>
                    </div>
                </div>
                <div class="dropdown">
                    <a href="LogOutEBooks" class="dropbtn" style="text-decoration: none">LOGOUT</a>
                </div>
            </div>
            <div class="centered">
                <hr/>
                <hr class="hr2"/>
                <h1>ADMIN</h1>
                <hr class="hr2"/>
                <hr/>
            </div>
            </br></br>
            <form method="get" enctype="text/plain" action="AdminButtons">
                <div align="center" style="overflow-x:auto;">
                    <table border="1" cellpadding="5">
                        <c:if test="${not empty rowsUsers.rows}">
                            <tr>
                                <th></th>
                                <th>USER NAME</th>
                                <th>ROLE</th>
                                <th>PASSWORD</th>
                                <th>INSERT</th>
                                <th>UPDATE</th>
                                <th>DELETE</th>
                            </tr>
                        </c:if>
                        <c:forEach var="user" items="${rowsUsers.rows}">
                            <tr>
                                <td><input type="checkbox" name="checkbox_users" value="${user.user_name}"></input></td>
                                <td><c:out value="${user.user_name}"/></td>
                                <td><c:out value="${user.user_role}"/></td>
                                <td><c:out value="${user.password}"/></td>
                                <td><c:out value="${user.ins}"/></td>
                                <td><c:out value="${user.upd}"/></td>
                                <td><c:out value="${user.del}"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <br/>
                    <table border="1" cellpadding="5">
                        <tr>
                            <th>USER NAME</th>
                            <th>ROLE</th>
                            <th>PASSWORD</th>
                            <th>INSERT</th>
                            <th>UPDATE</th>
                            <th>DELETE</th>
                        </tr>
                        <tr>
                            <td><input type="text" name="insert_user_name"/></td>
                            <td><select name="insert_user_role">
                                    <option value="none"></option>
                                    <option value="admin">admin</option>
                                    <option value="user">user</option>
                            </select></td>
                            <td><input type="text" name="insert_password"/></td>
                            <td><select name="insert_insert">
                                    <option value="none"></option>
                                    <option value="true">true</option>
                                    <option value="false">false</option>
                            </select></td>
                            <td><select name="insert_update">
                                    <option value="none"></option>
                                    <option value="true">true</option>
                                    <option value="false">false</option>
                            </select></td>
                            <td><select name="insert_delete">
                                    <option value="none"></option>
                                    <option value="true">true</option>
                                    <option value="false">false</option>
                            </select></td>
                        </tr>
                    </table>
                </div>
                <input type="submit" value="INSERT" id="submit1" name="insert_button"/>
                <input type="submit" value="UPDATE" id="submit2" name="update_button"/>
                <input type="submit" value="DELETE" id="submit3" name="delete_button"/>
            </form>
            <footer>Copyright &copy; ScoalaInformala.com</footer>  
        </c:when>
        <c:otherwise>
            <c:redirect url="./index.jsp"></c:redirect>
        </c:otherwise>
    </c:choose>
</body>
</html>
