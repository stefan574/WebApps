/*
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
public class ProductsButtons extends HttpServlet {

    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:derby://localhost:1527/ASF";
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
        ResultSet resultSet = null;
        
        // <editor-fold defaultstate="collapsed" desc="detele button">
        if (request.getParameter("delete_button") != null) {
            String[] checkboxes = request.getParameterValues("checkbox_products");
        
            if (checkboxes != null) {
                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    statement = connection.createStatement();
                    for (String id : checkboxes)
                        statement.execute("DELETE FROM ROOT.PRODUCTS WHERE ID = " + id);
                }
                catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                }
                finally {
                    if (statement != null)
                        try {
                            statement.close();
                        }
                        catch (SQLException e) {
                             Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                    if (connection != null)
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                }
                request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);   
            }
            else
                request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
        }
        // </editor-fold>
        else
            // <editor-fold defaultstate="collapsed" desc="update button">
            if (request.getParameter("insert_button") != null) {
                boolean exit = false;
                
                String[] values = {request.getParameter("insert_name"), 
                    request.getParameter("insert_price"), 
                    request.getParameter("insert_color"), 
                    request.getParameter("insert_stock"), 
                    request.getParameter("insert_expiration_date"), 
                    request.getParameter("insert_type")};
                
                if (values[0].equalsIgnoreCase("") || values[1].equalsIgnoreCase("")
                        || values[4].equalsIgnoreCase("") || values[2].equalsIgnoreCase("none")
                        || values[3].equalsIgnoreCase("none") || values[5].equalsIgnoreCase("none")
                        || values[4].equals("YYYY-MM-DD")) {
                    exit = true;
                }
                
                if (!exit)
                    if (!checkLegalValue("word", values[0]) 
                            && !checkLegalValue("number", values[1])
                            && !checkLegalValue("date", values[4]))
                        exit = true;
                
                if (exit)
                    request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                else {
                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        statement = connection.createStatement();
                        
                        resultSet = statement.executeQuery("SELECT MAX(ID) FROM PRODUCTS");
                        resultSet.next();
                        int max_id = resultSet.getInt(1) + 1;
                        
                        resultSet = statement.executeQuery("SELECT ID FROM ROOT.TYPES WHERE NAME = '" + values[5] + "'");
                        resultSet.next();
                        int type = resultSet.getInt(1);
                        
                        statement.execute("INSERT INTO ROOT.PRODUCTS (ID, NAME, PRICE, COLOR, "
                                + "STOCK, EXPIRATION_DATE, TYPE) VALUES ( " + max_id 
                                + ", '" + values[0] + "', " + Double.parseDouble(values[1]) 
                                + ", '" + values[2] + "', " + values[3] + ", '" + values[4] 
                                + "', " + type + ")");
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                    }
                    finally {
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (statement != null)
                            try {
                                statement.close();
                            }
                            catch (SQLException e) {
                                 Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (connection != null)
                            try {
                                connection.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                    }
                    request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                }
            }
            // </editor-fold>
            else
                // <editor-fold defaultstate="collapsed" desc="insert button">
                if (request.getParameter("update_button") != null) {
                    String[] checkboxes = request.getParameterValues("checkbox_products");
                    
                    if (checkboxes != null) {
                        
                        List<Parameter> list = new ArrayList<>();

                        String name = request.getParameter("insert_name");
                        if (!"".equals(name))
                            if (checkLegalValue("word", name))
                                list.add(new Parameter("NAME", name));
                        
                        String price = request.getParameter("insert_price");
                        if (!"".equals(price))
                            if (checkLegalValue("number", price))
                                list.add(new Parameter("PRICE", price));
                        
                        String color = request.getParameter("insert_color");
                        if (!"none".equals(color))
                            list.add(new Parameter("COLOR", color));
                        
                        String stock = request.getParameter("insert_stock");
                        if (!"none".equals(stock))
                            list.add(new Parameter("STOCK", stock));
                        
                        String expiration_date = request.getParameter("insert_expiration_date");
                        if (!"".equals(expiration_date) && !"YYYY-MM-DD".equals(expiration_date))
                            if (checkLegalValue("date", expiration_date))
                                list.add(new Parameter("EXPIRATION_DATE", expiration_date));
                        
                        String type = request.getParameter("insert_type");
                        if (!"none".equals(type))
                            list.add(new Parameter("TYPE", type));
                        
                        if (list.isEmpty())
                            request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                        else  {
                            try {
                                Class.forName(DRIVER);
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                statement = connection.createStatement();
                                for (String id : checkboxes)
                                    for (Parameter param : list)
                                        switch (param.getName()) {
                                            case "NAME":
                                            case "COLOR":
                                            case "EXPIRATION_DATE":
                                                statement.execute("UPDATE ROOT.PRODUCTS SET \""
                                                        + param.getName() + "\" = '"  
                                                        + param.getValue() + "'"
                                                                + " WHERE ID = " + id);
                                                break;
                                            case "PRICE":
                                            case "STOCK":
                                                statement.execute("UPDATE ROOT.PRODUCTS SET \""
                                                        + param.getName() + "\" = "
                                                        + param.getValue()
                                                        + " WHERE ID = " + id);
                                                break;
                                            case "TYPE":
                                                resultSet = statement.executeQuery("SELECT ID "
                                                        + "FROM ROOT.TYPES WHERE NAME = '"
                                                        + param.getValue() + "'");
                                                resultSet.next();
                                                int typeToModify = resultSet.getInt(1);
                                                statement.execute("UPDATE ROOT.PRODUCTS SET \""
                                                        + param.getName() + "\" = "
                                                        + typeToModify
                                                        + " WHERE ID = " + id);
                                                break;
                                            default:
                                                break;
                                    }
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                            finally {
                                if (resultSet != null)
                                    try {
                                        resultSet.close();
                                    }
                                catch (SQLException e) {
                                    Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                                }
                                if (statement != null)
                                    try {
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                         Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                if (connection != null)
                                    try {
                                        connection.close();
                                    }
                                    catch (SQLException e) {
                                        Logger.getLogger(ProductsButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                            }
                            request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                        }
                    }
                    else {
                        request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                    }
                    // </editor-fold>
                }
                else 
                    if (request.getParameter("previous_button") != null) {
                        int index = Integer.parseInt(request.getSession().getAttribute("index").toString()) - 1;
                        request.getSession().setAttribute("index", index);
                        request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                    }
                    else 
                        if (request.getParameter("next_button") != null) {
                            int index = Integer.parseInt(request.getSession().getAttribute("index").toString()) + 1;
                            request.getSession().setAttribute("index", index);
                            request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
                        }
    }

    // <editor-fold defaultstate="collapsed" desc="utilities">
    private boolean checkLegalValue(String requestString, String string) {
        switch (requestString) {
            case "word":
                String _string = string.trim().replaceAll(" +", "");
                for(int i = 0; i < _string.length() - 1; i++)
                    if (!Character.isLetter(_string.charAt(i))
                            && !Character.isDigit(_string.charAt(i))) {
                        return false;
                    }
                break;
            case "number":
                if (!isDouble(string))
                    return false;
                break;
            case "date":
                if (string.length() != 10 && string.charAt(4) != '-'
                        && string.charAt(7) != '-' && !isInt(string.substring(0, 5))
                        && !isInt(string.substring(8)))
                    return false;
                break;
            default:
                break;
        }
        return true;
    }
    
    /*
     * Uses a pattern to verify if given object can be converted to
     * a double type.
     */
    private boolean isDouble(String string) {
        String pattern = "-?\\d+\\.?\\d*";
        return Pattern.matches(pattern, string);
    }
    
    /*
     * Uses a pattern to verify if given object can be converted to
     * an int type.
     */
    private boolean isInt(String string) {
        String pattern = "-?\\d+";
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
    // </editor-fold>
    
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
        return "ProductsButtonsServlet";
    }// </editor-fold>

}
