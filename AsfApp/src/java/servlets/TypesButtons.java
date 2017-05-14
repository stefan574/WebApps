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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan
 */
public class TypesButtons extends HttpServlet {

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
        
        // <editor-fold defaultstate="collapsed" desc="delete button">
        if (request.getParameter("delete_button") != null) {
            String[] checkboxes = request.getParameterValues("checkbox_types");
        
            if (checkboxes != null) {
                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    statement = connection.createStatement();
                    for (String id : checkboxes)
                        statement.execute("DELETE FROM ROOT.TYPES WHERE ID = " + id);
                }
                catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                }
                finally {
                    if (statement != null)
                        try {
                            statement.close();
                        }
                        catch (SQLException e) {
                             Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                    if (connection != null)
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                }
                request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);   
            }
            else
                request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);
        }
        // </editor-fold>
        else
            // <editor-fold defaultstate="collapsed" desc="insert button">
            if (request.getParameter("insert_button") != null) {
                boolean exit = false;
                
                String[] values = {request.getParameter("insert_name"), 
                    request.getParameter("insert_description")};
                
                if (values[0].equalsIgnoreCase("")) {
                    exit = true;
                }
                
                if (!exit)
                    if (!checkLegalValue(values[0]))
                        exit = true;
                    else
                        if (values[1].equalsIgnoreCase(""))
                            values[1] = "No Description!";
                        else
                            if (!checkLegalValue(values[1]))
                                exit = true;
                
                if (exit)
                    request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);
                else {
                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        statement = connection.createStatement();
                        
                        resultSet = statement.executeQuery("SELECT MAX(ID) FROM TYPES");
                        resultSet.next();
                        int max_id = resultSet.getInt(1) + 1;
                        statement.execute("INSERT INTO ROOT.TYPES (ID, NAME, DESCRIPTION) "
                                + "VALUES (" + max_id + ", '" + values[0] + "', '" + values[1] + "')");
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                    }
                    finally {
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (statement != null)
                            try {
                                statement.close();
                            }
                            catch (SQLException e) {
                                 Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (connection != null)
                            try {
                                connection.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                    }
                    request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);
                }
            }
            // </editor-fold>
            else
                // <editor-fold defaultstate="collapsed" desc="update button">
                if (request.getParameter("update_button") != null) {
                    String[] checkboxes = request.getParameterValues("checkbox_types");
                    
                    if (checkboxes != null) {
                        
                        List<Parameter> list = new ArrayList<>();

                        String name = request.getParameter("insert_name");
                        if (!"".equals(name))
                            if (checkLegalValue(name))
                                list.add(new Parameter("NAME", name));
                        
                        String description = request.getParameter("insert_description");
                        if (!"".equals(description))
                            if (checkLegalValue(description))
                                list.add(new Parameter("DESCRIPTION", description));

                        if (list.isEmpty())
                            request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);
                        else  {
                            try {
                                Class.forName(DRIVER);
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                statement = connection.createStatement();
                                for (String id : checkboxes)
                                    for (Parameter param : list)
                                        statement.execute("UPDATE ROOT.TYPES SET \""
                                                + param.getName() + "\" = '"  
                                                + param.getValue() + "'"
                                                + " WHERE ID = " + id);
                                     
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                            finally {
                                if (statement != null)
                                    try {
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                         Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                if (connection != null)
                                    try {
                                        connection.close();
                                    }
                                    catch (SQLException e) {
                                        Logger.getLogger(TypesButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                            }
                            request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);
                        }
                    }
                    else {
                        request.getRequestDispatcher("./redirectTypes.jsp").forward(request, response);
                    }
                }
        // </editor-fold>
    }
    
    // <editor-fold defaultstate="collapsed" desc="utilities">
    private boolean checkLegalValue(String string) {
        String _string = string.trim().replaceAll(" +", "");
            for(int i = 0; i < _string.length() - 1; i++)
                if (!Character.isLetter(_string.charAt(i))
                        && !Character.isDigit(_string.charAt(i)))
                    return false;
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
        return "TypesButtonsServlet";
    }// </editor-fold>

}
