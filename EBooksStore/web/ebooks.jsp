<%-- 
    Document   : EBooks
    Created on : Mar 20, 2017, 5:43:23 PM
    Author     : Stefan-Alexandru Rentea
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<!DOCTYPE html>
<head>
    <title>EBooks Page</title>
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
    <sql:setDataSource
        var="dataSourceEBooks"
        driver="org.apache.derby.jdbc.ClientDriver"
        url="jdbc:derby://localhost:1527/EBOOKS_DB"
        user="root" password="root"
        />
    <sql:query var="rowsEBooks" dataSource="${dataSourceEBooks}">
        SELECT EBOOKS.ISBN, EBOOKS.TITLE, EBOOKS.NUMBER_OF_PAGES, EBOOKS.PRICE, 
        EBOOKS.RATING, TYPES.NAME AS TYPE FROM EBOOKS 
        JOIN TYPES ON EBOOKS.TYPE = TYPES.ID
    </sql:query>
     <sql:setDataSource
        var="dataSourceTypes"
        driver="org.apache.derby.jdbc.ClientDriver"
        url="jdbc:derby://localhost:1527/EBOOKS_DB"
        user="root" password="root"
        />
    <sql:query var="rowsTypes" dataSource="${dataSourceTypes}">
        SELECT NAME FROM ROOT.TYPES
    </sql:query>
    <div class="blacklined">
	<div class="dropdown">
            <a href="index.jsp" class="dropbtn" style="text-decoration: none">HOME</a>
	</div>
	<div class="dropdown">
            <button class="dropbtn">TABLES</button>
            <div class="dropdown-content">
		<a href="authors.jsp">AUTHORS</a>
		<a href="ratings.jsp">RATINGS</a>
            </div>
	</div>
	<div class="dropdown">
            <button class="dropbtn">CONTACT</button>
	</div>
    </div>
    <div class="centered">
        <hr/>
        <hr class="hr2"/>
        <h1>EBOOKS</h1>
        <hr class="hr2"/>
        <hr/>
    </div>
    </br></br>
    <form method="get" enctype="text/plain" action="EBooksButtons">
        <div align="center" style="overflow-x:auto;">
            <table border="1" cellpadding="5">
                <tr>
                    <th></th>
                    <th>ISBN</th>
                    <th>TITLE</th>
                    <th>NUMBER OF PAGES</th>
                    <th>PRICE</th>
                    <th>RATING</th>
                    <th>TYPE</th>
                </tr>
                <c:forEach var="ebook" items="${rowsEBooks.rows}">
                    <tr>
                        <td><input type="checkbox" name="checkbox_ebooks" value="${ebook.isbn}"></input></td>
                        <td><c:out value="${ebook.isbn}"/></td>
                        <td><c:out value="${ebook.title}"/></td>
                        <td><c:out value="${ebook.number_of_pages}"/></td>
                        <td><c:out value="${ebook.price}"/></td>
                        <td><c:out value="${ebook.rating}"/></td>
                        <td><c:out value="${ebook.type}"/></td>
                    </tr>
                </c:forEach>
            </table>
            <br/>
            <table border="1" cellpadding="5">
                <tr>
                    <th>TITLE</th>
                    <th>NUMBER OF PAGES</th>
                    <th>PRICE</th>
                    <th>TYPE</th>
                </tr>
                <tr>
                    <td><input type="text" name="insert_title"/></td>
                    <td><input type="text" name="insert_number_of_pages"/></td>
                    <td><input type="text" name="insert_price"/></td>
                    <td><select name="insert_type">
                        <option value="none"></option>
                        <c:forEach var="types" items="${rowsTypes.rows}">
                            <option value="${types.name}"><c:out value="${types.name}"/></option>
                        </c:forEach>
                    </select></td>
                </tr>
            </table>
        </div>
        <input type="submit" value="INSERT" id="submit1" name="insert_button"/>
        <input type="submit" value="UPDATE" id="submit2" name="update_button"/>
        <input type="submit" value="DELETE" id="submit3" name="delete_button"/>
    </form>
    <footer>Copyright &copy; ScoalaInformala.com</footer>   
</body>
</html>
