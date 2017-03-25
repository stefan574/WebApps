<%-- 
    Document   : redirectEBooks
    Created on : Mar 23, 2017, 7:36:11 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>EBooks redirect</title>
    </head>
    <body>
        <c:choose>
            <c:when test="${validUser == true}">
                <c:redirect url="ebooks.jsp"/>
            </c:when>
            <c:otherwise>
                <c:redirect url="./index.jsp"></c:redirect>
            </c:otherwise>
        </c:choose>  
    </body>
</html>
