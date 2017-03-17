<%-- 
    Document   : displayTable
    Created on : Mar 15, 2017, 8:38:06 PM
    Author     : Stefan
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page errorPage="exception.jsp" %> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Display Table using jsp</title>
        <link rel="stylesheet" type="text/css" href="./CSS/table.css">
    </head>
    <body>
        <sql:setDataSource
        var="dataSource"
        driver="org.apache.derby.jdbc.ClientDriver"
        url="jdbc:derby://localhost:1527/EBOOKS_DB"
        user="root" password="root"
        />

        <sql:query var="rows"   dataSource="${dataSource}">
            select EBOOKS.ISBN, EBOOKS.TITLE, EBOOKS.NUMBER_OF_PAGES, EBOOKS.PRICE, 
            EBOOKS.RATING, TYPES.NAME as TYPE from EBOOKS 
            join TYPES on EBOOKS.TYPE = TYPES.ID
        </sql:query>
        <div align="center">
            <table border="1" cellpadding="5">
                <caption><h2>List of EBooks</h2></caption>
                <tr>
                    <th>ISBN</th>
                    <th>TITLE</th>
                    <th>NUMBER OF PAGES</th>
                    <th>PRICE</th>
                    <th>RATING</th>
                    <th>TYPE</th>
                </tr>
                <c:forEach var="ebook" items="${rows.rows}">
                    <tr>
                        <td><c:out value="${ebook.isbn}" /></td>
                        <td><c:out value="${ebook.title}" /></td>
                        <td><c:out value="${ebook.number_of_pages}" /></td>
                        <td><c:out value="${ebook.price}" /></td>
                        <td><c:out value="${ebook.rating}" /></td>
                        <td><c:out value="${ebook.type}" /></td>
                    </tr>
                </c:forEach>
            </table>
        </div>    
    </body>
</html>
