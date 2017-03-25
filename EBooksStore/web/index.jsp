<%-- 
    Document   : index
    Created on : Mar 24, 2017, 11:32:20 AM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Login Page</title>
        <meta name="description" content="Presentation Layer for EBooks Project"/>
        <meta name="keywords" content="HTML, CSS, Java, Presentation Layer"/>
        <meta name="author" content="Stefan-Alexandru Rentea"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link rel="stylesheet" type="text/css" href="./CSS/css.css"/>
        <link rel="stylesheet" type="text/css" href="./CSS/login_form.css"/>
    </head>
<body>
    <div class="centered">
        <hr/>
        <hr class="hr2"/>
        <h1>EBOOKS STORE</h1>
        <hr class="hr2"/>
        <hr/>
    </div>
    <div id="divForm">
        <form method="GET" enctype="text/plain" action="LoginEBooks">
            <label>USER:</label>
            <input type="text" name="insert_user" placeholder="Your user name.."/>
            <label>PASSWORD:</label>
            <input type="password" name="insert_password" placeholder="Your password.."/>
            <input type="submit" value="LOGIN" name="login_button"/>
        </form>
    </div>
    <footer>Copyright &copy; ScoalaInformala.com</footer>
</body>
</html>
