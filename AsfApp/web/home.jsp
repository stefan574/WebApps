<%-- 
    Document   : home
    Created on : May 13, 2017, 11:09:43 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Home Page</title>
        <meta name="description" content="Presentation Layer for EBooks Project"/>
        <meta name="keywords" content="HTML, CSS, Java, Presentation Layer"/>
        <meta name="author" content="Stefan-Alexandru Rentea"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link rel="stylesheet" type="text/css" href="./CSS/css.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/menu.css"/>
    </head>
    <body>
        <c:set var="query" scope="session" value="${initialQuery}" />
        <c:set var="index" scope="session" value="${0}" />
        <div class="blacklined">
            <div class="dropdown">
                <a href="index.jsp" class="dropbtn" style="text-decoration: none">HOME</a>
            </div>
            <div class="dropdown">
                <button class="dropbtn">TABLES</button>
                <div class="dropdown-content">
                    <a href="types.jsp">TYPES</a>
                    <a href="products.jsp">PRODUCTS</a>
                </div>
            </div>
        </div>
        <div class="centered">
            <hr/>
            <hr class="hr2"/>
            <h1>TYPES/PRODUCTS PROJECT</h1>
            <hr class="hr2"/>
            <hr/>
        </div> 
    </body>
</html>