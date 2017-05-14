<%-- 
    Document   : products
    Created on : May 10, 2017, 10:21:12 PM
    Author     : Stefan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Asf App</title>
        <meta name="description" content="Presentation Layer for Asf Project"/>
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
        var="dataSourceProducts"
        driver="org.apache.derby.jdbc.ClientDriver"
        url="jdbc:derby://localhost:1527/ASF"
        user="root" password="root"
    />
    <sql:query var="rowsProducts" dataSource="${dataSourceProducts}">
        <c:out value="${query} ORDER BY ID OFFSET ${index*5} ROWS FETCH NEXT 5 ROWS ONLY" escapeXml="false" />
    </sql:query>
    <sql:query var="rowsProductsAux" dataSource="${dataSourceProducts}">
        <c:out value="${query}" escapeXml="false" />
    </sql:query>
    <c:set var="totalNumberOfRows" scope="session" value="${rowsProductsAux.rowCount}" />
    <c:set var="currentNumberOfRows" scope="session" value="${rowsProducts.rowCount}" />
    <sql:setDataSource
        var="dataSourceTypes"
        driver="org.apache.derby.jdbc.ClientDriver"
        url="jdbc:derby://localhost:1527/ASF"
        user="root" password="root"
    />
    <sql:query var="rowsTypes" dataSource="${dataSourceTypes}">
        SELECT NAME FROM TYPES
    </sql:query>    
    <div class="blacklined">
        <div class="dropdown">
            <a href="home.jsp" class="dropbtn" style="text-decoration: none">HOME</a>
        </div>
        <div class="dropdown">
            <button class="dropbtn">TABLES</button>
            <div class="dropdown-content">
                <a href="types.jsp">TYPES</a>
                <a href="products.jsp">PRODUCTS</a>
            </div>
        </div>
    </div>
    <br/><br/>
    <form method="get" enctype="text/plain" action="ProductsButtons">
        <div align="center" style="overflow-x:auto;">
            <table border="1" cellpadding="5">
                <c:if test="${not empty rowsProducts.rows}">
                    <tr>
                        <th></th>
                        <th>ID</th>
                        <th>NAME</th>
                        <th>PRICE</th>
                        <th>COLOR</th>
                        <th>STOCK</th>
                        <th>EXPIRATION_DATE</th>
                        <th>TYPE</th>
                    </tr>
                </c:if>
                <c:forEach var="product" items="${rowsProducts.rows}">
                    <tr>
                        <td><input type="checkbox" name="checkbox_products" value="${product.id}" /></td>
                        <td><c:out value="${product.id}"/></td>
                        <td><c:out value="${product.name}"/></td>
                        <td><c:out value="${product.price}"/></td>
                        <td><c:out value="${product.color}"/></td>
                        <td><c:out value="${product.stock}"/></td>
                        <td><c:out value="${product.expiration_date}"/></td>
                        <td><c:out value="${product.type}"/></td>
                    </tr>
                </c:forEach>
            </table>
            <c:if test="${index > 0}">
                <input type="submit" value="PREVIOUS PAGE(${index - 1})" name="previous_button"/>
            </c:if>
            <c:if test ="${(totalNumberOfRows - (currentNumberOfRows + (index * 5))) > 0}">
                <input type="submit" value="NEXT PAGE(${index + 1})" name="next_button"/>
            </c:if>
            <br/>
            <table border="1" cellpadding="5">
                <tr>
                    <th>NAME</th>
                    <th>PRICE</th>
                    <th>COLOR</th>
                    <th>STOCK</th>
                    <th>EXPIRATION_DATE</th>
                    <th>TYPE</th>
                </tr>
                <tr>
                    <td><input type="text" name="insert_name"/></td>
                    <td><input type="text" name="insert_price"/></td>
                    <td><select name="insert_color">
                        <option value="none" />
                        <option value="RED" /><c:out value="RED"/>
                        <option value="ORANGE" /><c:out value="ORANGE"/>
                        <option value="YELLOW" /><c:out value="YELLOW"/>
                        <option value="GREEN" /><c:out value="GREEN"/>
                        <option value="BLUE" /><c:out value="BLUE"/>
                        <option value="INDIGO" /><c:out value="INDIGO"/>
                        <option value="VIOLET" /><c:out value="VIOLET"/>
                    </select></td>
                    <td><select name="insert_stock">
                        <option value="none" />
                        <option value="true" /><c:out value="true"/>
                        <option value="false" /><c:out value="false"/>
                    </select></td>
                    <td><input type="text" name="insert_expiration_date" placeholder="YYYY-MM-DD" /></td>
                    <td><select name="insert_type">
                        <option value="none" />
                        <c:forEach var="types" items="${rowsTypes.rows}">
                           <option value="${types.name}" /><c:out value="${types.name}"/>
                        </c:forEach>
                    </select></td>
                </tr>
            </table>
        </div>
        <input type="submit" value="INSERT" id="submit1" name="insert_button"/>
        <input type="submit" value="UPDATE" id="submit2" name="update_button"/>
        <input type="submit" value="DELETE" id="submit3" name="delete_button"/>
    </form>
    <br/><br/><br/><br/>
    <form method="get" enctype="text/plain" action="SearchButton"> 
        <div align="center" style="overflow-x:auto;">
            <table border="1" cellpadding="5">
                <tr>
                    <th>NAME</th>
                    <th>COMPARE_PRICE</th>
                    <th>PRICE</th>
                    <th>COLOR</th>
                    <th>STOCK</th>
                    <th>EXPIRATION_DATE</th>
                    <th>TYPE</th>
                </tr>
                <tr>
                    <td><input type="text" name="search_name"/></td>
                    <td><select name="search_compare_price">
                        <option value="none" />
                        <option value="LT" /><c:out value="LT"/>
                        <option value="GT" /><c:out value="GT"/>
                        <option value="EQ" /><c:out value="EQ"/>
                    </select></td>
                    <td><input type="text" name="search_price"/></td>
                    <td><select name="search_color">
                        <option value="none" />
                        <option value="RED" /><c:out value="RED"/>
                        <option value="ORANGE" /><c:out value="ORANGE"/>
                        <option value="YELLOW" /><c:out value="YELLOW"/>
                        <option value="GREEN" /><c:out value="GREEN"/>
                        <option value="BLUE" /><c:out value="BLUE"/>
                        <option value="INDIGO" /><c:out value="INDIGO"/>
                        <option value="VIOLET" /><c:out value="VIOLET"/>
                    </select></td>
                    <td><select name="search_stock">
                        <option value="none" />
                        <option value="true" /><c:out value="true"/>
                        <option value="false" /><c:out value="false"/>
                    </select></td>
                    <td><input type="text" name="search_expiration_date_start" placeholder="YYYY-MM-DD"/>
                    <input type="text" name="search_expiration_date_stop" placeholder="YYYY-MM-DD" /></td>
                    <td><select name="search_type">
                        <option value="none" /><c:out value="ANY"/>
                        <c:forEach var="types" items="${rowsTypes.rows}">
                           <option value="${types.name}" /><c:out value="${types.name}"/>
                        </c:forEach>
                    </select></td>
                </tr>
            </table>
            <input type="submit" value="SEARCH" id="submit2" name="search_button"/>
        </div>
    </form>
    <footer>Copyright &copy; asf.ro</footer>
</body>
</html>
