<%-- 
    Document   : index
    Created on : May 13, 2017, 1:55:05 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Index Page</title>
    </head>
    <body>
        <c:set var="index" scope="session" value="${0}" />
        <c:set var="initialQuery" scope="session" value="SELECT PRODUCTS.ID, PRODUCTS.NAME, 
            PRODUCTS.PRICE, PRODUCTS.COLOR, PRODUCTS.STOCK, PRODUCTS.EXPIRATION_DATE, 
            TYPES.NAME AS TYPE FROM PRODUCTS JOIN TYPES ON PRODUCTS.TYPE = TYPES.ID" />
        <c:set var="query" scope="session" value="${initialQuery}" />
        <c:redirect url="home.jsp"/> 
    </body>
</html>
