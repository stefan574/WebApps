/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * @author Stefan-Alexandru Rentea
 */
public class EBooksButtons extends HttpServlet {

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
            String[] checkboxes = request.getParameterValues("checkbox_ebooks");
        
            if (checkboxes != null) {
                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    statement = connection.createStatement();
                    for (String isbn : checkboxes) {
                        statement.execute("DELETE FROM ROOT.EBOOKS WHERE ISBN = '" + isbn + "'");
                        statement.execute("DELETE FROM ROOT.AUTHORS WHERE ISBN = '" + isbn + "'");
                        statement.execute("DELETE FROM ROOT.RATINGS WHERE ISBN = '" + isbn + "'");
                    }
                }
                catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                }
                finally {
                    if (statement != null)
                        try {
                            statement.close();
                        }
                        catch (SQLException e) {
                             Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                    if (connection != null)
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                }
                request.getRequestDispatcher("./ebooks.jsp").forward(request, response);   
            }
            else
                request.getRequestDispatcher("./ebooks.jsp").forward(request, response);
        }
        else
            if (request.getParameter("insert_button") != null) {
                boolean exit = false;
                int type = 0;
                
                String[] values = {request.getParameter("insert_title"), 
                    request.getParameter("insert_number_of_pages"), 
                    request.getParameter("insert_price")};
                
                for (String value : values)
                    if (value.equalsIgnoreCase("")) {
                        exit = true;
                        break;
                    }
                
                if (!exit)
                    if (!checkLegalValue("title", values[0]))
                        exit = true;
                    else
                        if (!checkLegalValue("numberOfPages", values[1]))
                            exit = true;
                        else 
                            if (!checkLegalValue("price", values[2]))
                                exit = true;
                
                if (!exit)
                    if (request.getParameter("insert_type").equalsIgnoreCase("Novel"))
                        type = 1;
                    else 
                        if (request.getParameter("insert_type").equalsIgnoreCase("Technical"))
                            type = 2;
                        else
                            if (request.getParameter("insert_type").equalsIgnoreCase("Art Album"))
                                type = 3;
                        else
                            exit = true;
                
                if (exit)
                    request.getRequestDispatcher("./ebooks.jsp").forward(request, response);
                else {
                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        statement = connection.createStatement();
                        statement.execute("INSERT INTO ROOT.EBOOKS (ISBN, TITLE, NUMBER_OF_PAGES, "
                                + "PRICE, RATING, \"TYPE\") VALUES ('" + getIsbnValue() + "', '" + values[0] 
                                + "', " + Integer.parseInt(values[1]) + ", " 
                                + Double.parseDouble(values[2]) + ", " 
                                + "0.0, " 
                                + type + ")");
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                    }
                    finally {
                        if (statement != null)
                            try {
                                statement.close();
                            }
                            catch (SQLException e) {
                                 Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (connection != null)
                            try {
                                connection.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                    }
                    request.getRequestDispatcher("./ebooks.jsp").forward(request, response);
                }
            }
            else
                if (request.getParameter("update_button") != null) {
                    String[] checkboxes = request.getParameterValues("checkbox_ebooks");
                    
                    if (checkboxes != null) {
                        
                        List<Parameter> list = new ArrayList<>();

                        String title = request.getParameter("insert_title");
                        if (!"".equals(title))
                            if (checkLegalValue("title", title))
                                list.add(new Parameter("TITLE", title));
                        String numberOfPages = request.getParameter("insert_number_of_pages");
                        if (!"".equals(numberOfPages))
                            if (checkLegalValue("numberOfPages", numberOfPages))
                                list.add(new Parameter("NUMBER_OF_PAGES", numberOfPages));
                        String price = request.getParameter("insert_price");
                        if (!"".equals(price))
                            if (checkLegalValue("price", price))
                                list.add(new Parameter("PRICE", price));
                        if (request.getParameter("insert_type").equalsIgnoreCase("Novel"))
                                list.add(new Parameter("TYPE", "1"));
                            else 
                                if (request.getParameter("insert_type").equalsIgnoreCase("Technical"))
                                    list.add(new Parameter("TYPE", "2"));
                                else
                                    if (request.getParameter("insert_type").equalsIgnoreCase("Art Album"))
                                        list.add(new Parameter("TYPE", "3"));

                        if (list.isEmpty())
                            request.getRequestDispatcher("./ebooks.jsp").forward(request, response);
                        else  {
                            try {
                                Class.forName(DRIVER);
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                statement = connection.createStatement();
                                for (String isbn : checkboxes)
                                    for (Parameter param : list)
                                        switch (param.getName()) {
                                        case "TITLE":
                                            statement.execute("UPDATE ROOT.EBOOKS SET \""
                                                    + param.getName() + "\" = '"  
                                                    + param.getValue() + "'"
                                                            + " WHERE ISBN = '" + isbn + "'");
                                            break;
                                        case "NUMBER_OF_PAGES":
                                        case "TYPE":
                                            statement.execute("UPDATE ROOT.EBOOKS SET \""
                                                    + param.getName() + "\" = "
                                                    + Integer.parseInt(param.getValue())
                                                    + " WHERE ISBN = '" + isbn + "'");
                                            break;
                                        default:
                                            statement.execute("UPDATE ROOT.EBOOKS SET \""
                                                    + param.getName() + "\" = "
                                                    + Double.parseDouble(param.getValue())  
                                                    + " WHERE ISBN = '" + isbn + "'");
                                            break;
                                    } 
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                            finally {
                                if (statement != null)
                                    try {
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                         Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                if (connection != null)
                                    try {
                                        connection.close();
                                    }
                                    catch (SQLException e) {
                                        Logger.getLogger(EBooksButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                            }
                            request.getRequestDispatcher("./ebooks.jsp").forward(request, response);
                        }
                    }
                    else {
                        request.getRequestDispatcher("./ebooks.jsp").forward(request, response);
                    }
                }
    }

    private String getIsbnValue() {
        return ((int)(Math.random() * 899) + 100) + "-"
                + ((int)(Math.random() * 89) + 10) + "-"
                + ((int)(Math.random() * 89999) + 10000) + "-"
                + ((int)(Math.random() * 899) + 100);
    }
    
    private boolean checkLegalValue(String requestString, String string) {
        switch (requestString) {
            case "title":
                String _string = string.trim().replaceAll(" +", "");
                for(int i = 0; i < _string.length() - 1; i++)
                    if (!Character.isLetter(_string.charAt(i))
                            && !Character.isDigit(_string.charAt(i))) {
                        return false;
                    }
                break;
            case "numberOfPages":
                if (isInt(string)) {
                    if (!(Integer.parseInt(string) > 4
                            && Integer.parseInt(string) < 10000))
                        return false;
                }
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
        return "EBooksButtonsServlet";
    }// </editor-fold>

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
    
}
