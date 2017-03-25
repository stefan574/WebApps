<%-- 
    Document   : rating
    Created on : Mar 21, 2017, 8:39:51 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Ratings Page</title>
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
        <c:when test="${validUser == true}">
            <sql:setDataSource
                var="dataSourceRatings"
                driver="org.apache.derby.jdbc.ClientDriver"
                url="jdbc:derby://localhost:1527/EBOOKS_DB"
                user="root" password="root"
                />
            <sql:query var="rowsRatings" dataSource="${dataSourceRatings}">
                SELECT ISBN, USER_NAME, DESCRIPTION, RATING FROM ROOT.RATINGS
            </sql:query>
             <sql:setDataSource
                var="dataSourceEBooks"
                driver="org.apache.derby.jdbc.ClientDriver"
                url="jdbc:derby://localhost:1527/EBOOKS_DB"
                user="root" password="root"
                />
            <sql:query var="rowsIsbn" dataSource="${dataSourceEBooks}">
                SELECT ISBN FROM ROOT.EBOOKS
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
                    </div>
                </div>
                <div class="dropdown">
                    <a href="LogOutEBooks" class="dropbtn" style="text-decoration: none">LOGOUT</a>
                </div>
            </div>
            <div class="centered">
                <hr/>
                <hr class="hr2"/>
                <h1>RATINGS</h1>
                <hr class="hr2"/>
                <hr/>
            </div>
            </br></br>
            <form method="get" enctype="text/plain" action="RatingsButtons">
                <div align="center" style="overflow-x:auto;">
                    <table border="1" cellpadding="5">
                        <c:if test="${not empty rowsRatings.rows}">
                            <tr>
                                <th></th>
                                <th>ISBN</th>
                                <th>USER NAME</th>
                                <th>DESCRIPTION</th>
                                <th>RATING</th>
                            </tr>
                        </c:if>
                        <c:forEach var="rating" items="${rowsRatings.rows}">
                            <tr>
                                <c:choose>
                                    <c:when test="${(roleUser == 'admin') || (currentUser == rating.user_name)}">
                                        <td><input type="checkbox" name="checkbox_ratings" value="${rating.isbn}${rating.user_name}"></input></td>
                                    </c:when>
                                    <c:otherwise>
                                        <td></td>
                                    </c:otherwise>
                                </c:choose>
                                <td><c:out value="${rating.isbn}"/></td>
                                <td><c:out value="${rating.user_name}"/></td>
                                <td><c:out value="${rating.description}"/></td>
                                <td><c:out value="${rating.rating}"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <br/>
                    <table border="1" cellpadding="5">
                        <tr>
                            <th>ISBN</th>
                            <th>USER NAME</th>
                            <th>DESCRIPTION</th>
                            <th>RATING</th>
                        </tr>
                        <tr>
                            <td><select name="insert_isbn">
                                <option value="none"></option>
                                <c:forEach var="isbn" items="${rowsIsbn.rows}">
                                    <option value="${isbn.isbn}"><c:out value="${isbn.isbn}"/></option>
                                </c:forEach>
                            </select></td>
                            <c:choose>
                                    <c:when test="${(roleUser == 'admin') || (currentUser == rating.user_name)}">
                                        <td><input type="text" name="insert_user_name"/></td>
                                    </c:when>
                                    <c:otherwise>
                                    <td><input type="text" name="insert_user_name" placeholder="${currentUser}" readonly/></td>
                                    </c:otherwise>
                            </c:choose>
                            <td><input type="text" name="insert_description"/></td>
                            <td><input type="text" name="insert_rating"/></td>
                        </tr>
                    </table>
                </div>
                <c:if test="${insUser == true}">
                    <input type="submit" value="INSERT" id="submit1" name="insert_button"/>
                </c:if>
                <c:if test="${not empty rowsRatings.rows}">
                    <c:if test="${updUser == true}">
                        <input type="submit" value="UPDATE" id="submit2" name="update_button"/>
                    </c:if>
                    <c:if test="${delUser == true}">
                        <input type="submit" value="DELETE" id="submit3" name="delete_button"/>
                    </c:if>
                </c:if>
            </form>
            <footer>Copyright &copy; ScoalaInformala.com</footer>
        </c:when>
        <c:otherwise>
            <c:redirect url="./index.jsp"></c:redirect>
        </c:otherwise>
    </c:choose>  
</body>
</html>