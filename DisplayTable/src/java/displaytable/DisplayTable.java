/*
 * DisplayTable Servlet
 */
package displaytable;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Stefan-Alexandru Rentea
 */
public class DisplayTable extends HttpServlet {

    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:derby://localhost:1527/EBOOKS_DB";
    private static final String[] TABLES = {"AUTHORS", "EBOOKS", "RATINGS", "TYPES"};
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
        
        try (PrintWriter out = response.getWriter()) {
            List<EBookBean> list = new ArrayList<>();
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet DisplayTable</title>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" "
                    + "href=\"./CSS/table.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<div align=\"center\">\n"
                    + "<table border=\"1\" cellpadding=\"5\">\n"
                    + "<caption><h2>List of EBooks</h2></caption>\n"
                    + "<tr>\n"
                    + "<th>ISBN</th>\n"
                    + "<th>TITLE</th>\n"
                    + "<th>NUMBER OF PAGES</th>\n"
                    + "<th>PRICE</th>\n"
                    + "<th>RATING</th>\n"
                    + "<th>TYPE</th>\n"
                    + "</tr>");
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("select EBOOKS.ISBN, EBOOKS.TITLE, "
                        + "EBOOKS.NUMBER_OF_PAGES, EBOOKS.PRICE, EBOOKS.RATING, "
                        + "TYPES.NAME as TYPE from EBOOKS join TYPES "
                        + "on EBOOKS.TYPE = TYPES.ID");
                boolean resultSetHasRows = resultSet.next();
                if (resultSetHasRows) {
                    do {
                        EBookBean bean = new EBookBean();
                        bean.setIsbn(resultSet.getString(1));
                        bean.setTitle(resultSet.getString(2));
                        bean.setNumberOfPages(resultSet.getInt(3));
                        bean.setPrice(resultSet.getDouble(4));
                        bean.setRating(resultSet.getDouble(5));
                        bean.setType(resultSet.getString(6));
                        list.add(bean);
                    } while(resultSet.next());
                }
                else {
                    //System.out.println("EBOOKS Table is Empty!");
                }
            }
            catch (SQLException e) {
                //System.out.println(e);
            }
            finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    }
                    catch (SQLException ex) {
                        System.out.println(ex);
                    }
                }
                if (statement != null)
                    try {
                        statement.close();
                    }
                    catch (SQLException ex) {
                        System.out.println(ex);
                    }
                if (connection != null)
                    try {
                        connection.close();
                    }
                    catch (SQLException ex) {
                        System.out.println(ex);
                    }
            }
            
            list.stream().map((bean) -> {
                out.println("<tr>");
                return bean;
            }).map((bean) -> {
                out.println("<td>" + bean.getIsbn() + "</td>");
                return bean;
            }).map((bean) -> {
                out.println("<td>" + bean.getTitle() + "</td>");
                return bean;
            }).map((bean) -> {
                out.println("<td>" + bean.getNumberOfPages() + "</td>");
                return bean;
            }).map((bean) -> {
                out.println("<td>" + bean.getPrice() + "</td>");
                return bean;
            }).map((bean) -> {
                out.println("<td>" + bean.getRating() + "</td>");
                return bean;
            }).map((bean) -> {
                out.println("<td>" + bean.getType() + "</td>");
                return bean;
            }).forEachOrdered((_item) -> {
                out.println("</tr>\n");
            });
            
            out.println("</table>\n</div>");
            out.println("</body>");
            out.println("</html>");
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
    
    static void checkExistenceOfDatabase() {
        
        Connection connection = null;
        
        try {
            Class driverClass = Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            
            for (String string : TABLES)
                if(!checkExistenceOfTable(connection, string))
                    createTable(connection, string);
        }
        catch (ClassNotFoundException e) {
            //System.out.println("Missing required driver!");
        }
        catch (SQLException e) {
            //System.out.println(e);
        }
        finally {
            if (connection != null)
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    //System.out.println("Could not close the connection!");
                }
        }
    }
    
    private static boolean checkExistenceOfTable(Connection connection, String tableName) {
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
            //System.out.println(e);
        }
        return exists;
    }
    
    private static void createTable(Connection connection, String tableName) {
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
                            + "VALUES ('321-54-54321-54', 1, 'Mihail', 'Sadoveanu')");
                    statement.execute("INSERT INTO ROOT.AUTHORS ("
                            + "ISBN, ID, FIRST_NAME, LAST_NAME) "
                            + "VALUES ('999-12-12345-12', 2, 'Paul', 'Deitel')");
                    statement.execute("INSERT INTO ROOT.AUTHORS ("
                            + "ISBN, ID, FIRST_NAME, LAST_NAME) "
                            + "VALUES ('999-12-12345-12', 3, 'Harvey', 'Deitel')");
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
                            + " VALUES ('123-45-12345-12', 'Fratii Jderi', 792, 47.5, 4.0, 1)");
                    statement.execute("INSERT INTO ROOT.EBOOKS (ISBN, TITLE, "
                            + "NUMBER_OF_PAGES, PRICE, RATING, \"TYPE\")"
                            + " VALUES ('321-54-54321-54', 'Baltagul', 210, 22.0, 5.0, 1)");
                    statement.execute("INSERT INTO ROOT.EBOOKS (ISBN, TITLE, "
                            + "NUMBER_OF_PAGES, PRICE, RATING, \"TYPE\")"
                            + " VALUES ('999-12-12345-12', 'Java, How to program', 1535, 100.0, 2.0, 2)");
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
                            + "	VALUES ('123-45-12345-12', 'User1', 'E buna', 5.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('123-45-12345-12', 'User2', 'E in regula', 3.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('321-54-54321-54', 'User3', 'Cel mai bun roman', 5.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('999-12-12345-12', 'User1', 'Groaznic', 1.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('999-12-12345-12', 'User2', 'N-am inteles nimic', 2.0)");
                    statement.execute("INSERT INTO ROOT.RATINGS (ISBN, "
                            + "USER_NAME, DESCRIPTION, RATING) "
                            + "	VALUES ('999-12-12345-12', 'User4', 'Cat de cat ok', 3.0)");
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
                default :
                    // write in log file
                    break;
            }
        }
        catch (SQLException e) {
            //System.out.println("Could not create Table " + tableName);
        }
        finally {
            if (statement != null)
                try {
                    statement.close();
                }
                catch (SQLException ex) {
                    //System.out.println("Could not close statement!");
                }
        }
    }
    
    class EBookBean {
        
        private String isbn;
        private String title;
        private int numberOfPages;
        private double price;
        private double rating;
        private String type;

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getNumberOfPages() {
            return numberOfPages;
        }

        public void setNumberOfPages(int numberOfPages) {
            this.numberOfPages = numberOfPages;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        
    }
}
