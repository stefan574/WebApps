/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan
 */
public class LoginEBooks extends HttpServlet {

    private static final String[] TABLES = {"AUTHORS", "EBOOKS", "RATINGS", "TYPES", "USERS", "PERMISSIONS"};
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:derby://localhost:1527/EBOOKS_DB;create=true";
    private static final String DRIVER = "org.apache.derby.jdbc.ClientDriver";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            checkExistenceOfDatabase();
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String querry = "SELECT USER_NAME, PASSWORD, USER_ROLE FROM ROOT.USERS WHERE USER_NAME = ? AND PASSWORD = ?";
            statement = connection.prepareStatement(querry);
            statement.setString(1, request.getParameter("insert_user"));
            statement.setString(2, request.getParameter("insert_password"));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                request.getSession().setAttribute("validUser", true);
                request.getSession().setAttribute("roleUser", resultSet.getString(3));
                request.getSession().setAttribute("currentUser", request.getParameter("insert_user"));
                
                if (!resultSet.getString(3).equals("admin")) {
                    querry = "SELECT INS, UPD, DEL FROM ROOT.PERMISSIONS WHERE USER_NAME = ?";
                    statement = connection.prepareStatement(querry);
                    statement.setString(1, request.getParameter("insert_user"));
                    resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        request.getSession().setAttribute("insUser", resultSet.getBoolean(1));
                        request.getSession().setAttribute("updUser", resultSet.getBoolean(2));
                        request.getSession().setAttribute("delUser", resultSet.getBoolean(3));
                    }
                    else {
                        request.getSession().setAttribute("insUser", false);
                        request.getSession().setAttribute("updUser", false);
                        request.getSession().setAttribute("delUser", false);
                    }
                }
                else {
                    request.getSession().setAttribute("insUser", true);
                    request.getSession().setAttribute("updUser", true);
                    request.getSession().setAttribute("delUser", true);
                }
                
                request.getRequestDispatcher("./home.jsp").forward(request, response);
            }
            else
                request.getRequestDispatcher("./index.jsp").forward(request, response);
        }
        catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
                }
            if (statement != null)
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
                }
            if (connection != null)
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
                }
          }
    }

    private void checkExistenceOfDatabase() {
        
        Connection connection = null;
        
        try {
            Class driverClass = Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            
            for (String string : TABLES)
                if(!checkExistenceOfTable(connection, string))
                    createTable(connection, string);
        }
        catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if (connection != null)
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
                }
        }
    }
    
    private boolean checkExistenceOfTable(Connection connection, String tableName) {
        boolean exists = false;
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null)) {
            while (resultSet.next()) { 
                String _tableName = resultSet.getString("TABLE_NAME");
                if (_tableName != null && _tableName.equalsIgnoreCase(tableName)) {
                    exists = true;
                    break;
                }
            }
        }
        catch (SQLException e) { 
            Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
        }
        return exists;
    }
    
    private void createTable(Connection connection, String tableName) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            switch(tableName) {
                case "AUTHORS" :
                    statement.execute("CREATE TABLE AUTHORS ("
                            + "ISBN VARCHAR(16) NOT NULL, "
                            + "ID INTEGER NOT NULL, "
                            + "FIRST_NAME VARCHAR(32) NOT NULL, "
                            + "LAST_NAME VARCHAR(32) NOT NULL, "
                            + "PRIMARY KEY (ISBN, ID))");
                    statement.execute("INSERT INTO ROOT.AUTHORS ("
                            + "ISBN, ID, FIRST_NAME, LAST_NAME) "
                            + "VALUES ('321-54-54321-541', 1, 'Mihail', 'Sadoveanu')");
                    statement.execute("INSERT INTO ROOT.AUTHORS ("
                            + "ISBN, ID, FIRST_NAME, LAST_NAME) "
                            + "VALUES ('999-12-12345-121', 2, 'Paul', 'Deitel')");
                    statement.execute("INSERT INTO ROOT.AUTHORS ("
                            + "ISBN, ID, FIRST_NAME, LAST_NAME) "
                            + "VALUES ('999-12-12345-121', 3, 'Harvey', 'Deitel')");
                    break;
                case "EBOOKS" :
                    statement.execute("CREATE TABLE EBOOKS ("
                            + "ISBN VARCHAR(16) NOT NULL, "
                            + "TITLE VARCHAR(32) NOT NULL, "
                            + "NUMBER_OF_PAGES INTEGER NOT NULL, "
                            + "PRICE DOUBLE NOT NULL, "
                            + "RATING DOUBLE NOT NULL, "
                            + "TYPE INTEGER NOT NULL, "
                            + "PRIMARY KEY (ISBN))");
                    statement.execute("INSERT INTO ROOT.EBOOKS (ISBN, TITLE, "
                            + "NUMBER_OF_PAGES, PRICE, RATING, \"TYPE\")"
                            + " VALUES ('123-45-12345-121', 'Fratii Jderi', 792, 47.5, 4.0, 1)");
                    statement.execute("INSERT INTO ROOT.EBOOKS (ISBN, TITLE, "
                            + "NUMBER_OF_PAGES, PRICE, RATING, \"TYPE\")"
                            + " VALUES ('321-54-54321-541', 'Baltagul', 210, 22.0, 5.0, 1)");
                    statement.execute("INSERT INTO ROOT.EBOOKS (ISBN, TITLE, "
                            + "NUMBER_OF_PAGES, PRICE, RATING, \"TYPE\")"
                            + " VALUES ('999-12-12345-121', 'Java, How to program', 1535, 100.0, 2.0, 2)");
                    break;
                case "RATINGS" :
                    statement.execute("CREATE TABLE RATINGS ("
                            + "ISBN VARCHAR(16) NOT NULL, "
                            + "USER_NAME VARCHAR(32) NOT NULL, "
                            + "DESCRIPTION VARCHAR(256) NOT NULL, "
                            + "RATING DOUBLE NOT NULL, "
                            + "PRIMARY KEY (ISBN, USER_NAME))");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('123-45-12345-121', 'User1', 'E buna', 5.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('123-45-12345-121', 'User2', 'E in regula', 3.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('321-54-54321-541', 'User3', 'Cel mai bun roman', 5.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('999-12-12345-121', 'User1', 'Groaznic', 1.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('999-12-12345-121', 'User2', 'N-am inteles nimic', 2.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('999-12-12345-121', 'User4', 'Cat de cat ok', 3.0)");
                    break;
                case "TYPES" :
                    statement.execute("CREATE TABLE TYPES ("
                            + "ID INTEGER NOT NULL, "
                            + "NAME VARCHAR(32) NOT NULL, "
                            + "PRIMARY KEY (ID))");
                    statement.execute("INSERT INTO TYPES (ID, NAME) VALUES "
                            + "(1, 'Novel'), "
                            + "(2, 'Technical'), "
                            + "(3, 'Art Album')");
                    break;
                case "USERS" :
                    statement.execute("CREATE TABLE USERS (USER_NAME VARCHAR(32)"
                            + " NOT NULL, PASSWORD VARCHAR(32) NOT NULL,"
                            + " USER_ROLE VARCHAR(32) NOT NULL,"
                            + " PRIMARY KEY (USER_NAME))");
                    statement.execute("INSERT INTO ROOT.USERS (USER_NAME, PASSWORD, USER_ROLE) " 
                            + "VALUES ('admin', 'admin', 'admin'), ('user', 'user', 'user')");
                    break;
                case "PERMISSIONS" :
                    statement.execute("CREATE TABLE PERMISSIONS (USER_NAME VARCHAR(32) NOT NULL,"
                            + " INS BOOLEAN NOT NULL, UPD BOOLEAN NOT NULL,"
                            + " DEL BOOLEAN NOT NULL, PRIMARY KEY (USER_NAME))");
                    statement.execute("INSERT INTO ROOT.PERMISSIONS"
                            + " (USER_NAME, INS, UPD, DEL) VALUES"
                            + " ('admin', true, true, true),"
                            + " ('user', false, false, false)");
                    break;
                default :
                    // write in log file
                    break;
            }
        }
        catch (SQLException e) {
            Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if (statement != null)
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    Logger.getLogger(LoginEBooks.class.getName()).log(Level.SEVERE, null, e);
                }
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "LoginEBooksServlet";
    }// </editor-fold>

}
