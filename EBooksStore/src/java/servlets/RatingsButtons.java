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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan
 */
public class RatingsButtons extends HttpServlet {

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
            String[] checkboxes = request.getParameterValues("checkbox_ratings");
        
            if (checkboxes != null) {
                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    statement = connection.createStatement();
                    for (String isbn_user_name : checkboxes) {
                        statement.execute("DELETE FROM ROOT.RATINGS WHERE ISBN = '" 
                                + isbn_user_name.substring(0, 16) 
                                + "' AND USER_NAME = '" + isbn_user_name.substring(16) + "'");
                        calculateRating(statement, isbn_user_name.substring(0, 16));
                    }
                }
                catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                }
                finally {
                    if (statement != null)
                        try {
                            statement.close();
                        }
                        catch (SQLException e) {
                             Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                    if (connection != null)
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                }
                request.getRequestDispatcher("./ratings.jsp").forward(request, response);   
            }
            else
                request.getRequestDispatcher("./ratings.jsp").forward(request, response);
        }
        else
            if (request.getParameter("insert_button") != null) {
                boolean exit = false;
                
                String[] values = {request.getParameter("insert_isbn"), 
                    request.getParameter("insert_user_name"), 
                    request.getParameter("insert_description"),
                    request.getParameter("insert_rating")};
                
                if (request.getSession().getAttribute("roleUser").equals("user"))
                    values[1] = (String) request.getSession().getAttribute("currentUser");
                
                if (values[0].equals("none")) 
                        exit = true;
                else
                    if (values[1].equals("")) 
                        exit = true;
                    else
                        if (values[3].equals(""))
                            exit = true;
                
                if (values[2].equals(""))
                    values[2] = "No Description";
                
                if (!exit)
                    if (!checkLegalValue("title", values[1]))
                        exit = true;
                    else
                        if (!checkLegalValue("rating", values[3]))
                            exit = true;
                        else
                            if (!request.getParameter("insert_description").equals(""))
                                if (!checkLegalValue("title", values[2]))
                                    exit = true;
                
                if (exit)
                    request.getRequestDispatcher("./ratings.jsp").forward(request, response);
                else {
                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        statement = connection.createStatement();
                        statement.execute("INSERT INTO ROOT.RATINGS (ISBN, USER_NAME, "
                                + "DESCRIPTION, RATING) VALUES ('" + values[0] + "', '" 
                                + values[1] + "', '" + values[2] + "', " + values[3] + ")");
                        calculateRating(statement, values[0]);
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                    }
                    finally {
                        if (statement != null)
                            try {
                                statement.close();
                            }
                            catch (SQLException e) {
                                 Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (connection != null)
                            try {
                                connection.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                    }
                    request.getRequestDispatcher("./ratings.jsp").forward(request, response);
                }
            }
            else
                if (request.getParameter("update_button") != null) {
                    String[] checkboxes = request.getParameterValues("checkbox_ratings");
                    
                    if ((checkboxes != null 
                            && request.getParameter("insert_isbn").equals("none") 
                            && request.getParameter("insert_user_name").equals(""))
                            || (checkboxes == null 
                            && !request.getParameter("insert_isbn").equals("none") 
                            && !request.getParameter("insert_user_name").equals(""))
                            || (checkboxes == null 
                            && !request.getParameter("insert_isbn").equals("none") 
                            && request.getSession().getAttribute("roleUser").equals("user"))) {
                        
                        List<Parameter> list = new ArrayList<>();
                        
                        String description = request.getParameter("insert_description");
                        if (!"".equals(description))
                            if (checkLegalValue("title", description))
                                list.add(new Parameter("DESCRIPTION", description));
                        String rating = request.getParameter("insert_rating");
                        if (!"".equals(rating))
                            if (checkLegalValue("rating", rating))
                                list.add(new Parameter("RATING", rating));
                        
                        if (list.isEmpty())
                            request.getRequestDispatcher("./ratings.jsp").forward(request, response);
                        else  {
                            try {
                                Class.forName(DRIVER);
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                statement = connection.createStatement();
                                if (checkboxes == null)
                                    for (Parameter param : list)
                                        if (!request.getSession().getAttribute("roleUser").equals("user"))
                                            if (param.getName().equalsIgnoreCase("description"))
                                                statement.execute("UPDATE ROOT.RATINGS SET \"" 
                                                        + param.getName() + "\" = '" 
                                                        + param.getValue() + "' WHERE ISBN = '" 
                                                        + request.getParameter("insert_isbn") 
                                                        + "' AND USER_NAME = '" 
                                                        + request.getParameter("insert_user_name") + "'");
                                            else {
                                                statement.execute("UPDATE ROOT.RATINGS SET \"" 
                                                        + param.getName() + "\" = " 
                                                        + param.getValue() + " WHERE ISBN = '" 
                                                        + request.getParameter("insert_isbn") 
                                                        + "' AND USER_NAME = '" 
                                                        + request.getParameter("insert_user_name") + "'");
                                                calculateRating(statement, request.getParameter("insert_isbn"));
                                            }
                                        else 
                                            if (param.getName().equalsIgnoreCase("description"))
                                                statement.execute("UPDATE ROOT.RATINGS SET \"" 
                                                        + param.getName() + "\" = '" 
                                                        + param.getValue() + "' WHERE ISBN = '" 
                                                        + request.getParameter("insert_isbn") 
                                                        + "' AND USER_NAME = '" 
                                                        + (String) request.getSession().getAttribute("currentUser") + "'");
                                            else {
                                                statement.execute("UPDATE ROOT.RATINGS SET \"" 
                                                        + param.getName() + "\" = " 
                                                        + param.getValue() + " WHERE ISBN = '" 
                                                        + request.getParameter("insert_isbn") 
                                                        + "' AND USER_NAME = '" 
                                                        + (String) request.getSession().getAttribute("currentUser") + "'");
                                                calculateRating(statement, request.getParameter("insert_isbn"));
                                            }
                                else {
                                    for (String isbn_user_name : checkboxes)
                                        for (Parameter param : list)
                                            if (param.getName().equalsIgnoreCase("description"))
                                                statement.execute("UPDATE ROOT.RATINGS SET \"" 
                                                        + param.getName() + "\" = '" 
                                                        + param.getValue() + "' WHERE ISBN = '" 
                                                        + isbn_user_name.substring(0, 16)
                                                        + "' AND USER_NAME = '" 
                                                        + isbn_user_name.substring(16) + "'");
                                            else {
                                                statement.execute("UPDATE ROOT.RATINGS SET \"" 
                                                        + param.getName() + "\" = " 
                                                        + param.getValue() + " WHERE ISBN = '" 
                                                        + isbn_user_name.substring(0, 16)
                                                        + "' AND USER_NAME = '" 
                                                        + isbn_user_name.substring(16) + "'");
                                                calculateRating(statement, isbn_user_name.substring(0, 16));
                                            }
                                }
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                            finally {
                                if (statement != null)
                                    try {
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                         Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                if (connection != null)
                                    try {
                                        connection.close();
                                    }
                                    catch (SQLException e) {
                                        Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                            }
                            request.getRequestDispatcher("./ratings.jsp").forward(request, response);
                        }
                    }
                    else {
                        request.getRequestDispatcher("./ratings.jsp").forward(request, response);
                    }
                }
    }

    private void calculateRating(Statement statement, String isbn) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT AVG(RATING) FROM ROOT.RATINGS WHERE ISBN = '" + isbn + "'");
            resultSet.next();
            if (resultSet.getString(1) == null)
                statement.execute("UPDATE ROOT.EBOOKS SET \"RATING\" = 0 WHERE ISBN = '" + isbn + "'");
            else
                statement.execute("UPDATE ROOT.EBOOKS SET \"RATING\" = " 
                        + doPrecisionRating(resultSet.getString(1)) 
                        + " WHERE ISBN = '" + isbn + "'");
        }
        catch (SQLException e) {
            Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                    Logger.getLogger(RatingsButtons.class.getName()).log(Level.SEVERE, null, e);
                }
        }
    }
    
    private double doPrecisionRating(String string) {
        switch (string.substring(2, 3)) {
            case "0":
            case "1":
            case "2":
                return Double.parseDouble(string.substring(0, 2) + "0");
            case "8":
            case "9":
                return Double.parseDouble((Integer.parseInt(string.substring(0, 1)) + 1) + ".0");
            default: 
                return Double.parseDouble(string.substring(0, 2) + "5");
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
            case "numberOfPages":
                if (isInt(string))
                    if (!(Integer.parseInt(string) > 4
                            && Integer.parseInt(string) < 10000))
                        return false;
                else
                    return false;
                break;
            case "price":
                if (isDouble(string)) {
                    if (!(Double.parseDouble(string) > -1
                            && Double.parseDouble(string) < 100000))
                        return false;
                }
                else
                    return false;       
                break;
            case "rating":
                if (!string.matches("0|0.0|0.5|1|1.0|1.5|2|2.0|2.5|3|3.0|3.5|4|4.0|4.5|5|5.0"))
                    return false;    
                break;
            default:
                break;
        }
        return true;
    }
    
    /*
     * Uses a pattern to verify if given object can be converted to
     * an int type.
     */
    private boolean isInt(String string) {
        String pattern = "-?\\d+";
        return Pattern.matches(pattern, string);
    }

    /*
     * Uses a pattern to verify if given object can be converted to
     * a double type.
     */
    private boolean isDouble(String string) {
        String pattern = "-?\\d+\\.?\\d*";
        return Pattern.matches(pattern, string);
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
        return "RatingsButtonsServlet";
    }// </editor-fold>

}
