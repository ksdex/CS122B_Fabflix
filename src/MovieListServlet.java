import com.google.gson.JsonObject;
import funcScripts.HelperFunc;
import dataClass.SessionParamList;

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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movieList")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        SessionParamList paramList = new SessionParamList(request);
        if(action != null) {
            HelperFunc.printToConsole("action: ");
            HelperFunc.printToConsole(action);
            PrintWriter out = response.getWriter();
            try{
                HelperFunc.addToCartButton(out, action, request);
            } catch (Exception e) {
                // write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "fail");
                HelperFunc.printToConsole("Error: " + e.getMessage());
                out.write(jsonObject.toString());
                response.setStatus(200);
            }
            out.close();
        }
        else {
            doGet(request, response);
        }
    }


    private String getOrder(String firstSort, String firstSortOrder, String secondSort, String secondSortOrder){
        String order = "";
        if (firstSort == null) {
            order += " order by rating desc";
        } else {
            HelperFunc.printToConsole("Get here");
            order += " order by " + firstSort + " " + firstSortOrder;
            if (secondSort != null) {
                order += ", " + secondSort + " " + secondSortOrder;
            }
        }
        return order;
    }


    private String getLimit(String offset, String itemNum){
        String limit = "";
        if (offset == null) {
            limit += " limit 20 offset 0";
        } else {
            limit += " limit " + itemNum + " offset " + offset;
        }
        return limit;
    }

    private String getRidOfBlankInUrl(String tar){
        String result = tar;
        if(tar != null && tar.indexOf("%20") != -1){
            result = result.replace("%20", " ");
        }
        return result;
    }


    private String addBaseQueryConnector(boolean isFirstParam){
        if(!isFirstParam){
            return "and ";
        }
        else{
            return "where ";
        }
    }


    private String getSqlString(SessionParamList paramList, String order, String limit){
        String baseQuery = "select distinct m.id as movieid, m.title, m.year, m.director, r.rating from movies as m, ratings as r ";
        if(paramList.search != null){
            boolean isFirstParam = true;
            if(paramList.starname != null){
                baseQuery += ", stars_in_movies as sim, stars as s ";
                String movieStarRatingMatch = "where m.id = sim.movieId " + "and sim.starId = s.id " + "and r.movieId = m.id " +
                        "and s.name like '%"+paramList.starname+"%' ";
                baseQuery += movieStarRatingMatch;
                isFirstParam = false;
            }

            // Search for title
            if(paramList.title != null){
                String titleFormat =  "match(m.title) against ('?' in boolean mode";
                String[] titleList = paramList.title.split(" ");
                if(titleList.length == 1){
                    baseQuery += addBaseQueryConnector(isFirstParam) + "match(m.title) against ('+" + getRidOfBlankInUrl(paramList.title) + "*' in boolean mode";
                }
                else{
                    baseQuery += addBaseQueryConnector(isFirstParam) + "match(m.title) against (";
                    for(int i = 0; i < titleList.length; i++){
                        baseQuery += " '+" + titleList[i] + "*'";
                    }
                    baseQuery += " in boolean mode";
                }
                isFirstParam = false;
            }
            // Search for director
            if(paramList.director != null){
                baseQuery += addBaseQueryConnector(isFirstParam) + "m.director like '%" + getRidOfBlankInUrl(paramList.director) + "%'";
                isFirstParam = false;
            }
            // Search for year
            if(paramList.year != null){
                baseQuery += addBaseQueryConnector(isFirstParam) + "m.year = " + paramList.year;
                isFirstParam = false;
            }
            baseQuery += " and r.movieId = m.id ";
        }
        else if (paramList.genre != null){
            baseQuery += ", genres_in_movies as g " +
                            "where m.id = g.movieId " +
                            "and m.id = r.movieId " +
                            "and g.genreId = " + paramList.genre;
        }
        else if (paramList.startwith != null) {
            if(paramList.startwith.equals("none")) {
                baseQuery += " where title not REGEXP '^[0-9a-zA-Z]' and m.id = r.movieId ";
            }
            else {
                baseQuery += " where (title like '" + paramList.startwith + "%' or title like '" + paramList.startwith.toUpperCase() + "%')" +
                                " and m.id = r.movieId ";
            }
        }
        else{
            baseQuery += "where m.id = r.movieId ";
        }
        return baseQuery + order + limit;
    }


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            SessionParamList paramList = new SessionParamList(request);
            session.setAttribute("lastParamList", paramList);
            String order = getOrder(paramList.firstSort, paramList.firstSortOrder, paramList.secondSort, paramList.secondSortOrder);
            String limit = getLimit(paramList.offset, paramList.itemNum);
            HelperFunc.printToConsole(order);
            HelperFunc.printToConsole(limit);
            HelperFunc.printToConsole("code is here");

            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            // Declare our statement
            String query = getSqlString(paramList, order, limit);
            HelperFunc.printToConsole(query);

            // write JSON string to output
            out.write( HelperFunc.movieListTable(query, dbcon).toString() );
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
