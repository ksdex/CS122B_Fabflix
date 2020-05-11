package funcScripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataClass.SessionParamList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HelperFunc {
    static boolean debugMode = false;

    static public void printToConsole(Object tar){
        if(debugMode){
            System.out.println(tar);
        }
    }


    public static FileWriter fw = null;
    public static int logLineCount = 1;

    static public void initializeLogFile(String fileName){
        java.util.Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String d = df.format(day);
        String logFilePath = "./src/funcScripts/logs/log_" + d + "_" + fileName + ".log";
        File logFile = new File(logFilePath);
        try{
            fw = new FileWriter(logFile);
            logLineCount = 1;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static public void xmlHandlerLog(Object tar){
        try{
            java.util.Date day = new Date();
            SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String d = df_time.format(day);
            fw.write(Integer.toString(logLineCount) + " " + d + " " + tar.toString() + "\n");
            logLineCount++;
            fw.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    static public void closeLogFile(){
        try {
            fw.close();
            fw = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    static public String genreMap(String g){
        if(g.equalsIgnoreCase("susp")){
            return "Thriller";
        }
        else if(g.equalsIgnoreCase("cnr")){
            return "Cops And Robbers";
        }
        else if(g.equalsIgnoreCase("dram") || g.equalsIgnoreCase("draam") ||
                g.equalsIgnoreCase("ram") || g.equalsIgnoreCase("Dramn") ||
                g.equalsIgnoreCase("Drama") || g.equalsIgnoreCase("Dramd") ){
            return "Drama";
        }
        else if(g.equalsIgnoreCase("west") || g.equalsIgnoreCase("west1")){
            return "Western";
        }
        else if(g.equalsIgnoreCase("myst")){
            return "Mystery";
        }
        else if(g.equalsIgnoreCase("s.f.") || g.equalsIgnoreCase("ScFi") ||
                g.equalsIgnoreCase("SCIF") || g.equalsIgnoreCase("SxFi")){
            return "Sci-Fi";
        }
        else if(g.equalsIgnoreCase("advt")){
            return "Adventure";
        }
        else if(g.equalsIgnoreCase("horr")){
            return "Horror";
        }
        else if(g.equalsIgnoreCase("romt")){
            return "Romantic";
        }
        else if(g.equalsIgnoreCase("musc")){
            return "Musical";
        }
        else if(g.equalsIgnoreCase("docu")){
            return "Documentary";
        }
        else if(g.equalsIgnoreCase("porn")){
            return "Adult";
        }
        else if(g.equalsIgnoreCase("actn") || g.equalsIgnoreCase("act")){
            return "Action";
        }
        else if(g.equalsIgnoreCase("fant")){
            return "Fantasy";
        }
        else if(g.equalsIgnoreCase("noir")){
            return "Black";
        }
        else if(g.equalsIgnoreCase("biop") || g.equalsIgnoreCase("biob")){
            return "Biography";
        }
        else if(g.equalsIgnoreCase("tv")){
            return "Reality-TV";
        }
        else if(g.equalsIgnoreCase("tvs")){
            return "TV Series";
        }
        else if(g.equalsIgnoreCase("tvm")){
            return "TV Miniseries";
        }
        else if(g.equalsIgnoreCase("cart")){
            return "Animation";
        }
        else if(g.equalsIgnoreCase("hist")){
            return "History";
        }
        else if(g.equalsIgnoreCase("cnrb")){
            return "Cops and Robbers";
        }
        else if(g.equalsIgnoreCase("disa")){
            return "Disaster";
        }
        else if(g.equalsIgnoreCase("surr")){
            return "Surreal";
        }
        else{
            return firstInit(g);
        }
    }


    static public String firstInit(String s){
        if(s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        else{
            return s.toUpperCase();
        }
    }


    static public void addToCartButton(PrintWriter out, String action, HttpServletRequest request) throws IOException {
        HelperFunc.printToConsole("Post: action not null");
        HelperFunc.printToConsole(action.equals("addToCart"));
        if(action.equals("addToCart")) {
            String movieId = request.getParameter("movieId");
            HttpSession session = request.getSession();
            Map<String, float[]> cartItems = (Map<String, float[]>) session.getAttribute("cartItems");
            if (cartItems == null) {
                Map<String, float[]> result = new HashMap<String, float[]>();
                float[] temp = new float[2];
                temp[0] = 1;
                temp[1] = 0;
                result.put(movieId, temp);
                session.setAttribute("cartItems", result);
            } else {
                float[] temp = new float[2];
                if (cartItems.get(movieId) == null) {
                    temp[0] = 1;
                    temp[1] = 0;
                    cartItems.put(movieId, temp);
                } else {
                    temp[0] = cartItems.get(movieId)[0] + 1;
                    temp[1] = cartItems.get(movieId)[1];
                    cartItems.put(movieId, temp);
                }
                session.setAttribute("cartItems", cartItems);
            }
            HelperFunc.printToConsole(movieId);
            HelperFunc.printToConsole(cartItems);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            out.write(jsonObject.toString());
        }
    }

    static public JsonObject addStarButton(Connection dbcon, HttpServletRequest request) throws IOException, SQLException {
        String starname = request.getParameter("starname");
        String birthyear = request.getParameter("birthyear");
        PreparedStatement statement = dbcon.prepareStatement("select max(id) as maxid from stars");
        ResultSet rs = statement.executeQuery();
        String maxid = "0";
        while(rs.next()){
            maxid = rs.getString("maxid");
        }
        rs.close();
        statement.close();
        int id = Integer.parseInt(maxid.substring(maxid.length()-7));
        String sql = "insert into stars values (?,?,?)";
        PreparedStatement preparedStatement = dbcon.prepareStatement(sql);
        preparedStatement.setString(1,"nm"+(id+1));
        preparedStatement.setString(2,starname);
        int len = birthyear.length();
        boolean ii = birthyear.equals("");
        if(birthyear.equals("")){
            preparedStatement.setNull(3,Types.INTEGER);
        }
        else{
            preparedStatement.setInt(3,Integer.parseInt(birthyear));
        }

        int value = preparedStatement.executeUpdate();
        JsonObject jsonObject = new JsonObject();
        if(value > 0){
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("id", "nm"+(id+1));

        }
        else{
            jsonObject.addProperty("status", "fail");
        }
        return jsonObject;
    }

    static public JsonObject addMovieButton(Connection dbcon, HttpServletRequest request) throws IOException, SQLException {
        String title = request.getParameter("title");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String starname = request.getParameter("starname");
//        String searchMovie = "select * from movies where title = ?, year=?, director=?";
//        PreparedStatement moviePS = dbcon.prepareStatement(searchMovie);
//        moviePS.setString(1,title);
//        moviePS.setInt(2,year);
//        moviePS.setString(3,director);
//        ResultSet rsmovie = moviePS.executeQuery();
//        boolean dupMovie = false, dupStar = false, dupGenre=false;
//        if(rsmovie.next()){
//            dupMovie = true;
//        }
//        else{
//            String starid = null;
//            int genreid = 0;
//            String searchStar = "select * from stars where name = ?";
//            PreparedStatement starPS = dbcon.prepareStatement(searchStar);
//            starPS.setString(1,starname);
//            ResultSet rsstar = starPS.executeQuery();
//            if(rsstar.next()){
//                dupStar = true;
//                starid = rsstar.getString("id");
//            }
//            else{
//                dupStar = false;
//                JsonObject starjson = addStarButton(dbcon,request);
//                starid = starjson.get("id").toString();
//            }
//            String searchGenre = "select * from genres where name = ?";
//            PreparedStatement genrePS = dbcon.prepareStatement(searchGenre);
//            genrePS.setString(1,starname);
//            ResultSet rsstar = starPS.executeQuery();
//        }
//

//        if(dupMovie){
//            jsonObject.addProperty("status", "fail");
//            jsonObject.addProperty("message", "Cannot add duplicate movie");
//            return jsonObject;
//        }
        JsonObject jsonObject = new JsonObject();
        String sql = "call add_movie(?,?,?,?,?,?,?,?,?)";
        CallableStatement cstm = dbcon.prepareCall(sql);
        cstm.registerOutParameter(1, Types.INTEGER);
        cstm.registerOutParameter(2, Types.VARCHAR);
        cstm.registerOutParameter(3, Types.INTEGER);
        cstm.registerOutParameter(4, Types.VARCHAR);
        cstm.setString(5,title);
        cstm.setInt(6,Integer.parseInt(year));
        cstm.setString(7,director);
        cstm.setString(8,starname);
        cstm.setString(9,genre);
        cstm.execute();
        int result = cstm.getInt(1);
        if(result==1){
            String new_movie_id = cstm.getString(2);
            int new_genre_id = cstm.getInt(3);
            String new_star_id = cstm.getString(4);
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("new_movie_id",new_movie_id);
            jsonObject.addProperty("new_genre_id",new_genre_id);
            jsonObject.addProperty("new_star_id",new_star_id);
        }
        else{
            jsonObject.addProperty("status", "fail");
        }

        return jsonObject;
    }


    static public JsonObject sessionParamToJsonObject(SessionParamList paramList){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("search", paramList.search);
        jsonObject.addProperty("genreid", paramList.genre);
        jsonObject.addProperty("startwith", paramList.startwith);
        jsonObject.addProperty("firstSort", paramList.firstSort);
        jsonObject.addProperty("firstSortOrder", paramList.firstSortOrder);
        jsonObject.addProperty("secondSort", paramList.secondSort);
        jsonObject.addProperty("secondSortOrder", paramList.secondSortOrder);
        jsonObject.addProperty("offset", paramList.offset);
        jsonObject.addProperty("itemNum", paramList.itemNum);
        jsonObject.addProperty("starname", paramList.starname);
        jsonObject.addProperty("title", paramList.title);
        jsonObject.addProperty("year", paramList.year);
        jsonObject.addProperty("director", paramList.director);
        return jsonObject;
    }


    static public JsonArray movieListTable(String query, Connection dbcon) throws SQLException {
        PreparedStatement statement = dbcon.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        JsonArray jsonArray = new JsonArray();
        // Iterate through each row of rs
        while (rs.next()) {
            String movie_id = rs.getString("movieid");
            String movie_title = rs.getString("title");
            String movie_year = rs.getString("year");
            String movie_director = rs.getString("director");
            String movie_rating = rs.getString("rating");

            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_id", movie_id);
            jsonObject.addProperty("movie_title", movie_title);
            jsonObject.addProperty("movie_year", movie_year);
            jsonObject.addProperty("movie_director", movie_director);
            jsonObject.addProperty("movie_rating", movie_rating);
            PreparedStatement statement2 = dbcon.prepareStatement(
                    "select sim.starId, siom.name, count(sim.starId) from stars_in_movies as sim, (" +
                            "select starId, name from movies,stars,stars_in_movies where stars.id=stars_in_movies.starId " +
                            "and stars_in_movies.movieId=movies.id and movies.id='" + movie_id + "' " +
                            ")as siom " + // siom: stars_in_one_movie
                            "where sim.starId = siom.starId " +
                            "group by sim.starId " +
                            "order by count(sim.starId) desc, siom.name");
            ResultSet rs2 = statement2.executeQuery();
            JsonObject starsJsonObject = new JsonObject();
            int count = 1;
            while(rs2.next()){
                JsonObject singleStarJsonObject = new JsonObject();
                singleStarJsonObject.addProperty("id", rs2.getString("starId"));
                singleStarJsonObject.addProperty("name", rs2.getString("name"));
                starsJsonObject.add(Integer.toString(count), singleStarJsonObject);
                count += 1;
            }
            jsonObject.add("movie_stars", starsJsonObject);
            rs2.close();
            statement2.close();

            PreparedStatement statement3 = dbcon.prepareStatement("select * from movies,genres,genres_in_movies" +
                    " where genres.id=genres_in_movies.genreId and genres_in_movies.movieId=movies.id" +
                    " and movies.id='" + movie_id + "' order by genres.name");
            ResultSet rs3 = statement3.executeQuery();
            JsonObject genresJsonObject = new JsonObject();
            count = 1;
            while(rs3.next()){
                JsonObject oneGenresJsonObject = new JsonObject();
                oneGenresJsonObject.addProperty("name", rs3.getString("name"));
                oneGenresJsonObject.addProperty("genreId", rs3.getString("genreId"));
                genresJsonObject.add(Integer.toString(count), oneGenresJsonObject);
                count += 1;
            }
            jsonObject.add("movie_genres", genresJsonObject);
            rs3.close();
            statement3.close();
            jsonArray.add(jsonObject);
        }
        rs.close();
        statement.close();
        return jsonArray;
    }

}
