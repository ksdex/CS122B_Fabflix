import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class testdb {
    public static void main(String[] args) throws Exception {
        JsonObject json = new JsonObject();
        JsonObject json1 = new JsonObject();
        json1.addProperty("a","a");
        JsonObject json2 = new JsonObject();
        JsonObject json3 = new JsonObject();
        json.add("1",json1);
        System.out.println(json.get("1").getAsJsonObject().get("a").getAsString());
    }
}
