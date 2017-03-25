<%-- 
    Document   : redirectAuthors
    Created on : Mar 25, 2017, 4:20:18 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Authors redirect</title>
    </head>
    <body>
        <c:choose>
            <c:when test="${validUser == true}">
                <c:redirect url="authors.jsp"/>
            </c:when>
            <c:otherwise>
                <c:redirect url="./index.jsp"></c:redirect>
            </c:otherwise>
        </c:choose>  
    </body>
</html>
