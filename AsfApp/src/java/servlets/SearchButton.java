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
public class SearchButton extends HttpServlet {

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
        
        if (request.getParameter("search_button") != null) {
            String query = request.getSession().getAttribute("initialQuery").toString();
            List<String> query_list = new ArrayList<>();
            
            String name = request.getParameter("search_name");
            if (!"".equals(name))
                if (checkLegalValue("word", name))
                    query_list.add("PRODUCTS.NAME like '%" + name + "%'");
            
            String price = request.getParameter("search_price");
            String compare_price = request.getParameter("search_compare_price");
            if (!"".equals(price) && !"none".equals(compare_price) 
                    && checkLegalValue("number", price))
                if (compare_price.equals("LT"))
                    query_list.add("PRODUCTS.PRICE < " + price);
                else 
                    if (compare_price.equals("GT"))
                        query_list.add("PRODUCTS.PRICE > " + price);
                    else 
                        query_list.add("PRODUCTS.PRICE = " + price);
                        
            String color = request.getParameter("search_color");
                if (!"none".equals(color))
                    query_list.add("PRODUCTS.COLOR = '" + color + "'");
                        
            String stock = request.getParameter("search_stock");
                if (!"none".equals(stock))
                    query_list.add("PRODUCTS.STOCK = " + stock);
                        
            String expiration_date_start = request.getParameter("search_expiration_date_start");
            String expiration_date_stop = request.getParameter("search_expiration_date_stop");
                if (!"".equals(expiration_date_start) && !"YYYY-MM-DD".equals(expiration_date_start) 
                        && (!"".equals(expiration_date_stop) && !"YYYY-MM-DD".equals(expiration_date_stop)) 
                        && checkLegalValue("date", expiration_date_start) && checkLegalValue("date", expiration_date_stop))
                    query_list.add("PRODUCTS.EXPIRATION_DATE BETWEEN '" + expiration_date_start + "' and '" + expiration_date_stop + "'");
            
            int type = returnType(request.getParameter("search_type"));
            if (type != -1)
                query_list.add("PRODUCTS.TYPE = " + type);    
                        
            if (query_list.isEmpty()) {
                request.getSession().setAttribute("index", "0");
                request.getSession().setAttribute("query", query);
                request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
            }
            else {
                query += " WHERE " + query_list.get(0);
                for (int i = 1; i < query_list.size(); i++)
                    query += " AND " + query_list.get(i);
                request.getSession().setAttribute("index", "0");
                request.getSession().setAttribute("query", query);
                request.getRequestDispatcher("./redirectProducts.jsp").forward(request, response);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="utilities">
    private int returnType(String type) {
        int typeToReturn = -1;
        if (!"none".equals(type)) {
            String USER = "root";
            String PASSWORD = "root";
            String URL = "jdbc:derby://localhost:1527/ASF";
            String DRIVER = "org.apache.derby.jdbc.ClientDriver";
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT ID "
                        + "FROM ROOT.TYPES WHERE NAME = '"
                        + type + "'");
                resultSet.next();
                typeToReturn = resultSet.getInt(1);
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
        }
        return typeToReturn;
    }
    
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
        return "SearchButtonServlet";
    }// </editor-fold>

}
