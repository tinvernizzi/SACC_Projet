package fr.polytech;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

@WebServlet(name = "DownloadServlet")
public class DownloadServlet extends HttpServlet {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static ResourceBundle bundle = ResourceBundle.getBundle("configDispacher");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String fileId = request.getParameter("fileId");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("fileId", fileId);
        if (isNoob(userId)) {
            httpPost(bundle.getString("pushHandler.url"), jsonObject.toString());
        } else {
            httpPost(bundle.getString("pullHandler.url"), jsonObject.toString());
        }
        response.getWriter().print("Download link will send by email.");
    }

    private boolean isNoob(String userId) throws IOException {
        String score = "";
        while (score.equals("")) {
            score = getUserScore(userId);
        }
        return Integer.parseInt(score) <= 100;
    }

    private String getUserScore(String userId) throws IOException {
        URL obj = new URL(bundle.getString("userRegistry.url") + "?userId=" + userId);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject object = (JsonObject) new JsonParser().parse(response.toString());

            return object.get("userScore").toString();
        } else {
            System.out.println("GET request not worked");
        }

        return "";
    }

    private String httpPost(String url, String parameters) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
