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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan
 */
public class AdminButtons extends HttpServlet {

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
            String[] checkboxes = request.getParameterValues("checkbox_users");
        
            if (checkboxes != null) {
                try {
                    Class.forName(DRIVER);
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    statement = connection.createStatement();
                    
                    for (String user : checkboxes) 
                        if (!user.equals("admin")) {
                            statement.execute("DELETE FROM ROOT.USERS WHERE USER_NAME = '" + user + "'");
                            statement.execute("DELETE FROM ROOT.PERMISSIONS WHERE USER_NAME = '" + user + "'");
                        }
                }
                catch (SQLException | ClassNotFoundException e) {
                    Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                }
                finally {
                    if (statement != null)
                        try {
                            statement.close();
                        }
                        catch (SQLException e) {
                             Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                    if (connection != null)
                        try {
                            connection.close();
                        }
                        catch (SQLException e) {
                            Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                        }
                }
                request.getRequestDispatcher("./admin.jsp").forward(request, response);   
            }
            else
                request.getRequestDispatcher("./admin.jsp").forward(request, response);
        }
        else
            if (request.getParameter("insert_button") != null) {
                boolean exit = false;
                
                String userName = request.getParameter("insert_user_name");
                String userRole = request.getParameter("insert_user_role");
                String password = "1234";
                
                if (userName.equals(""))
                    exit = true;
                 else
                    if (!checkLegalValue("title", userName))
                        exit = true;
                
                if (!exit)
                    if (!request.getParameter("insert_password").equals(""))
                        if (!checkLegalValue("title", request.getParameter("insert_password")))
                            exit = true;
                        else 
                            password = request.getParameter("insert_password");
                
                if (exit)
                    request.getRequestDispatcher("./admin.jsp").forward(request, response);
                else {
                    try {
                        Class.forName(DRIVER);
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        statement = connection.createStatement();
                        if (userRole.equals("admin")) {
                            statement.execute("INSERT INTO ROOT.USERS (USER_NAME, PASSWORD, "
                                    + "USER_ROLE) VALUES ('" + userName + "', '" + password 
                                    + "', 'admin')");
                            statement.execute("INSERT INTO ROOT.PERMISSIONS (USER_NAME, INS, "
                                    + "UPD, DEL) VALUES ('" + userName + "', true, true, true)");
                        }
                        else {
                            boolean insert = request.getParameter("insert_insert").equals("true");
                            boolean update = request.getParameter("insert_update").equals("true");
                            boolean delete = request.getParameter("insert_delete").equals("true");
                            
                            statement.execute("INSERT INTO ROOT.USERS (USER_NAME, PASSWORD, "
                                    + "USER_ROLE) VALUES ('" + userName + "', '" + password 
                                    + "', 'user')");
                            statement.execute("INSERT INTO ROOT.PERMISSIONS (USER_NAME, INS, "
                                    + "UPD, DEL) VALUES ('" + userName + "', " + insert + ", " 
                                    + update + ", " + delete + ")");
                        }
                    }
                    catch (SQLException | ClassNotFoundException e) {
                        Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                    }
                    finally {
                        if (statement != null)
                            try {
                                statement.close();
                            }
                            catch (SQLException e) {
                                 Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                        if (connection != null)
                            try {
                                connection.close();
                            }
                            catch (SQLException e) {
                                Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                    }
                    request.getRequestDispatcher("./admin.jsp").forward(request, response);
                }
            }
            else
                if (request.getParameter("update_button") != null) {
                    String[] checkboxes = request.getParameterValues("checkbox_users");
                    
                    if ((checkboxes != null && request.getParameter("insert_user_name").equals(""))
                            || (checkboxes == null && !request.getParameter("insert_user_name").equals(""))) {
                        
                        List<Parameter> list = new ArrayList<>();
                        
                        String password = request.getParameter("insert_password");
                        if (!"".equals(password))
                            if (!checkLegalValue("title", password))
                                request.getRequestDispatcher("./admin.jsp").forward(request, response);
                        String insert = request.getParameter("insert_insert");
                        if (!"none".equals(insert))
                            list.add(new Parameter("INS", insert));
                        String update = request.getParameter("insert_update");
                        if (!"none".equals(update))
                            list.add(new Parameter("UPD", update));
                        String delete = request.getParameter("insert_delete");
                        if (!"none".equals(delete))
                            list.add(new Parameter("DEL", delete));
                        
                        if (list.isEmpty() && request.getParameter("insert_user_role").equals("none") && password.equals(""))
                            request.getRequestDispatcher("./admin.jsp").forward(request, response);
                        else  {
                            try {
                                Class.forName(DRIVER);
                                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                                statement = connection.createStatement();
                                if (checkboxes == null) {
                                    if ((request.getParameter("insert_user_name").equals("admin") 
                                            && request.getSession().getAttribute("currentUser").equals("admin"))
                                            || (!request.getParameter("insert_user_name").equals("admin"))) {
                                        if (!"".equals(password))
                                            statement.execute("UPDATE ROOT.USERS SET \"PASSWORD\" = '" 
                                                    + password + "' WHERE USER_NAME = '" 
                                                    + request.getParameter("insert_user_name") + "'");
                                        if (request.getParameter("insert_user_role").equals("admin")) {
                                            statement.execute("UPDATE ROOT.USERS SET \"USER_ROLE\" = "
                                                    + "'admin' WHERE USER_NAME = '" 
                                                    + request.getParameter("insert_user_name") + "'");
                                            statement.execute("UPDATE ROOT.PERMISSIONS SET \"INS\" = true, "
                                                    + "\"UPD\" = true, \"DEL\" = true WHERE USER_NAME = '" 
                                                    + request.getParameter("insert_user_name") + "'");
                                        }
                                        else {
                                            if (request.getParameter("insert_user_role").equals("user"))
                                                statement.execute("UPDATE ROOT.USERS SET \"USER_ROLE\" = "
                                                    + "'user' WHERE USER_NAME = '" 
                                                    + request.getParameter("insert_user_name") + "'");
                                            ResultSet resultSet = null;
                                                try {
                                                    resultSet = statement.executeQuery("SELECT USER_ROLE "
                                                        + "FROM USERS WHERE USER_NAME = '" 
                                                        + request.getParameter("insert_user_name") + "'");
                                                    if (resultSet.next() && !resultSet.getString(1).equals("admin") && !list.isEmpty())
                                                        for (Parameter param : list)
                                                            statement.execute("UPDATE ROOT.PERMISSIONS SET \""
                                                                    + param.getName() + "\" = " + param.getValue()
                                                                    + " WHERE USER_NAME = '"
                                                                    + request.getParameter("insert_user_name") + "'");
                                                }
                                                catch (SQLException e) {
                                                    Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                                                }
                                                finally {
                                                    if (resultSet != null)
                                                        try {
                                                            resultSet.close();
                                                        }
                                                        catch (SQLException e) {
                                                             Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                                                        }
                                                }
                                        }
                                    }
                                }
                                else {
                                    for (String user : checkboxes) 
                                        if ((user.equals("admin") 
                                                && request.getSession().getAttribute("currentUser").equals("admin"))
                                                || (!user.equals("admin"))) {
                                            if (!"".equals(password))
                                                statement.execute("UPDATE ROOT.USERS SET \"PASSWORD\" = '" 
                                                        + password + "' WHERE USER_NAME = '" 
                                                        + user + "'");
                                            if (request.getParameter("insert_user_role").equals("admin")) {
                                                statement.execute("UPDATE ROOT.USERS SET \"USER_ROLE\" = "
                                                        + "'admin' WHERE USER_NAME = '" 
                                                        + user + "'");
                                                statement.execute("UPDATE ROOT.PERMISSIONS SET \"INS\" = true, "
                                                        + "\"UPD\" = true, \"DEL\" = true WHERE USER_NAME = '" 
                                                        + user + "'");
                                            }
                                            else {
                                                if (request.getParameter("insert_user_role").equals("user"))
                                                    statement.execute("UPDATE ROOT.USERS SET \"USER_ROLE\" = "
                                                        + "'user' WHERE USER_NAME = '" 
                                                        + user + "'");
                                                ResultSet resultSet = null;
                                                try {
                                                    resultSet = statement.executeQuery("SELECT USER_ROLE "
                                                        + "FROM USERS WHERE USER_NAME = '" 
                                                        + user + "'");
                                                    if (resultSet.next() && !resultSet.getString(1).equals("admin") && !list.isEmpty())
                                                        for (Parameter param : list)
                                                            statement.execute("UPDATE ROOT.PERMISSIONS SET \""
                                                                    + param.getName() + "\" = " + param.getValue()
                                                                    + " WHERE USER_NAME = '"
                                                                    + user + "'");
                                                }
                                                catch (SQLException e) {
                                                    Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                                                }
                                                finally {
                                                    if (resultSet != null)
                                                        try {
                                                            resultSet.close();
                                                        }
                                                        catch (SQLException e) {
                                                             Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                                                        }
                                                }
                                            }
                                        }
                                }
                            }
                            catch (SQLException | ClassNotFoundException e) {
                                Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                            }
                            finally {
                                if (statement != null)
                                    try {
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                         Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                if (connection != null)
                                    try {
                                        connection.close();
                                    }
                                    catch (SQLException e) {
                                        Logger.getLogger(AdminButtons.class.getName()).log(Level.SEVERE, null, e);
                                    }
                            }
                            request.getRequestDispatcher("./admin.jsp").forward(request, response);
                        }
                    }
                    else {
                        request.getRequestDispatcher("./admin.jsp").forward(request, response);
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
        return "Short description";
    }// </editor-fold>

}
