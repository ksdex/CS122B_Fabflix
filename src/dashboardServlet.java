import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class dashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonArray result = new JsonArray();
        try {
            HelperFunc.printToConsole("dashboard code is here");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "show tables where Tables_in_moviedb != 'customer_save'";
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                String tablename = rs.getString("Tables_in_moviedb");
                Statement statement2 = dbcon.createStatement();
                ResultSet rs2 = statement2.executeQuery("describe "+tablename);
                while(rs2.next()){
                    JsonObject tableObject = new JsonObject();
                    tableObject.addProperty("tablename",tablename);
                    tableObject.addProperty("fieldname", rs2.getString("Field"));
                    tableObject.addProperty("type", rs2.getString("Type"));
                    result.add(tableObject);
                }
            }
            out.write( result.toString() );
            // set response status to 200 (OK)
            response.setStatus(200);
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        HelperFunc.printToConsole("MovieListServlet?");
        out.close();
    }
}
