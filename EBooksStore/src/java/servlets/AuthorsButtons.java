/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan-Alexandru Rentea
 */
public class AuthorsButtons extends HttpServlet {

    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:derby://localhost:1527/EBOOKS_DB";
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
        Statement statement = null;
        
        if (request.getParameter("delete_button") != null) {
            String[] checkboxes = request.getParameterValues("checkbox_authors");
        
            if (checkboxes != null) {
                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    statement = connection.createStatement();
                    for (String isbn_id : checkboxes)
                        statement.execute("DELETE FROM ROOT.AUTHORS WHERE ISBN = '" 
                                + isbn_id.substring(0, 16) 
                                + "' AND ID = " + isbn_id.substring(16));
                }
                catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                }
                finally {
                    if (statement != null)
                        try {
                            statement.close();
                        }
                        catch (SQLException e) {
                             Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                    if (connection != null)
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                }
                request.getRequestDispatcher("./authors.jsp").forward(request, response);   
            }
            else
                request.getRequestDispatcher("./authors.jsp").forward(request, response);
        }
        else
            if (request.getParameter("insert_button") != null) {
                boolean exit = false;
                
                String[] values = {request.getParameter("insert_isbn"), 
                    request.getParameter("insert_id"), 
                    request.getParameter("insert_first_name"),
                    request.getParameter("insert_last_name")};
                
                if (!values[0].equals("none"))
                    if (!values[1].equals("none") && ((!values[2].equals("") && !values[3].equals(""))
                            || (values[2].equals("") && !values[3].equals(""))
                            || (!values[2].equals("") && values[3].equals(""))))
                        exit = true;
                    else {
                        if (values[1].equals("none") && ((values[2].equals("") && values[3].equals(""))
                                || (!values[2].equals("") && values[3].equals(""))
                                || (values[2].equals("") && !values[3].equals(""))))
                            exit = true;
                    }
                else 
                    exit = true;
                
                if (!exit)
                    if (!checkLegalValue("title", values[2]))
                        exit = true;
                    else
                        if (!checkLegalValue("title", values[3]))
                            exit = true;
                
                if (exit)
                    request.getRequestDispatcher("./redirectAuthors.jsp").forward(request, response);
                else {
                    ResultSet resultSet = null;
                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        statement = connection.createStatement();
                        if (!values[1].equals("none")) {
                            resultSet = statement.executeQuery("SELECT FIRST_NAME, LAST_NAME"
                                    + " FROM ROOT.AUTHORS WHERE "
                                    + "ID = " + Integer.parseInt(values[1]) + "FETCH FIRST 1 ROWS ONLY");
                            resultSet.next();
                            statement.execute("INSERT INTO ROOT.AUTHORS (ISBN, ID, FIRST_NAME, LAST_NAME)"
                                    + " VALUES ('" + values[0] + "', " + Integer.parseInt(values[1]) + ", '" 
                                    + resultSet.getString(1) + "', '" + resultSet.getString(2) + "')");
                        }
                        else {
                            resultSet = statement.executeQuery("SELECT MAX(ID) FROM ROOT.AUTHORS");
                            if (resultSet != null) {
                                resultSet.next();
                                statement.execute("INSERT INTO ROOT.AUTHORS (ISBN, ID, FIRST_NAME, LAST_NAME)"
                                        + " VALUES('" + values[0] + "', " + (resultSet.getInt(1) + 1)
                                        + ", '" + values[2] + "', '" + values[3] + "')");
                            }
                            else {
                                statement.execute("INSERT INTO ROOT.AUTHORS (ISBN, ID, FIRST_NAME, LAST_NAME)"
                                        + " VALUES('" + values[0] + "', " + 1
                                        + ", '" + values[2] + "', '" + values[3] + "')");
                            }
                        }
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                    }
                    finally {
                        if (statement != null)
                            try {
                                statement.close();
                            }
                            catch (SQLException e) {
                                 Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (connection != null)
                            try {
                                connection.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                    }
                    request.getRequestDispatcher("./redirectAuthors.jsp").forward(request, response);
                }
            }
            else
                if (request.getParameter("update_button") != null) {
                    String[] checkboxes = request.getParameterValues("checkbox_authors");
                    
                    if ((checkboxes != null && request.getParameter("insert_id").equals("none"))
                            || (checkboxes == null && !request.getParameter("insert_id").equals("none"))) {
                        
                        List<Parameter> list = new ArrayList<>();
                        
                        String first_name = request.getParameter("insert_first_name");
                        if (!"".equals(first_name))
                            if (checkLegalValue("title", first_name))
                                list.add(new Parameter("FIRST_NAME", first_name));
                        String last_name = request.getParameter("insert_last_name");
                        if (!"".equals(last_name))
                            if (checkLegalValue("title", last_name))
                                list.add(new Parameter("LAST_NAME", last_name));
                        
                        if (list.isEmpty())
                            request.getRequestDispatcher("./authors.jsp").forward(request, response);
                        else  {
                            try {
                                Class.forName(DRIVER);
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                statement = connection.createStatement();
                                if (checkboxes == null)
                                    for (Parameter param : list)
                                        statement.execute("UPDATE ROOT.AUTHORS SET \"" 
                                                + param.getName() + "\" = '" 
                                                + param.getValue() + "' WHERE ID = " 
                                                + request.getParameter("insert_id"));
                                else {
                                    Set<String> set = new HashSet<>();
                                    for (String id : checkboxes)
                                        set.add(id.substring(16));
                                    for (String id : set)
                                        for (Parameter param : list)
                                            statement.execute("UPDATE ROOT.AUTHORS SET \"" 
                                                    + param.getName() + "\" = '" 
                                                    + param.getValue() + "' WHERE ID = " 
                                                    + id);
                                }
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                            finally {
                                if (statement != null)
                                    try {
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                         Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                if (connection != null)
                                    try {
                                        connection.close();
                                    }
                                    catch (SQLException e) {
                                        Logger.getLogger(AuthorsButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                            }
                            request.getRequestDispatcher("./authors.jsp").forward(request, response);
                        }
                    }
                    else {
                        request.getRequestDispatcher("./authors.jsp").forward(request, response);
                    }
                }
    }

    private boolean checkLegalValue(String requestString, String string) {
        switch (requestString) {
            case "title":
                String _string = string.trim().replaceAll(" +", "");
                for(int i = 0; i < _string.length() - 1; i++)
                    if (!Character.isLetter(_string.charAt(i))
                            && !Character.isDigit(_string.charAt(i)))
                        return false;
                break;
            default:
                break;
        }
        return true;
    }
    
    private class Parameter {
        
        private final String name;
        private final String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
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
        return "AuthorsButtonsServlet";
    }// </editor-fold>

}
