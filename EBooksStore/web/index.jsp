<%-- 
    Document   : index
    Created on : Mar 20, 2017, 5:06:36 PM
    Author     : Stefan-Alexandru Rentea
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
    <title>Main Page</title>
    <meta name="description" content="Presentation Layer for EBooks Project"/>
    <meta name="keywords" content="HTML, CSS, Java, Presentation Layer"/>
    <meta name="author" content="Stefan-Alexandru Rentea"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link rel="stylesheet" type="text/css" href="./CSS/css.css"/>
    <link rel="stylesheet" type="text/css" href="./CSS/menu.css"/>
</head>
<body>
    <div class="blacklined">
	<div class="dropdown">
            <button class="dropbtn" onclick="window.location = 'index.jsp'">HOME</button>
	</div>
	<div class="dropdown">
            <button class="dropbtn">TABLES</button>
            <div class="dropdown-content">
		<a href="ebooks.jsp">EBOOKS</a>
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
        <h1>EBOOKS STORE</h1>
        <hr class="hr2"/>
        <hr/>
    </div>
    
    <footer>Copyright &copy; ScoalaInformala.com</footer>
    
</body>
</html>
