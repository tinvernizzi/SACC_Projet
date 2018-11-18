package fr.polytech.tasks;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import fr.polytech.business.DownloadRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class QueueTask extends HttpServlet {

  private static final String USER_AGENT = "Mozilla/5.0";
  private static ResourceBundle bundle = ResourceBundle.getBundle("configPushHandler");

  private static final String URL_FILE = bundle.getString("fileRegistry.url");

  private static final String URL_MAIL = bundle.getString("mailer.url");

  private static final String URL_USER = bundle.getString("userRegistry.url");

  private List<String> articles = new ArrayList<>();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String json = new Gson().toJson(articles);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().write(json);

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    DownloadRequest downloadRequest = new Gson().fromJson(request.getReader(), DownloadRequest.class);

    processRequest(downloadRequest);
  }

  private void processRequest(DownloadRequest request){
    try {
      JsonObject user = getUserFromGET(request.getUserId());

      if(user.get("canOperate").getAsBoolean()) {
        //Get from file registry
        String link;

        link = sendGET(request.getFileId());

        articles.add(link);

        sendPOST(user.get("userEmailAdress").getAsString(), link);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private JsonObject getUserFromGET(long userId) throws IOException {
    URL obj = new URL(URL_USER + userId);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", USER_AGENT);
    int responseCode = con.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) { // success
      BufferedReader in = new BufferedReader(new InputStreamReader(
              con.getInputStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      System.out.println(response.toString());

      return (JsonObject) new JsonParser().parse(response.toString());
    } else {
      System.out.println("GET request not worked");
    }

    return new JsonObject();
  }

  private String sendGET(String fileId) throws IOException {
    URL obj = new URL(URL_FILE + fileId);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("User-Agent", USER_AGENT);
    int responseCode = con.getResponseCode();

    if (responseCode == HttpURLConnection.HTTP_OK) { // success
      BufferedReader in = new BufferedReader(new InputStreamReader(
              con.getInputStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JsonObject object = (JsonObject) new JsonParser().parse(response.toString());

      return object.get("fileUrl").toString();
    } else {
      System.out.println("GET request not worked");
    }

    return "";
  }

  private void sendPOST(String address, String link) throws IOException {
    URL obj = new URL(URL_MAIL);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", USER_AGENT);

    // For POST only - START
    con.setDoOutput(true);
    OutputStream os = con.getOutputStream();

    String params = "address=" + address + "&subject=PolyShare&content=" + link;

    os.write(params.getBytes());
    os.flush();
    os.close();
    // For POST only - END

    con.getResponseCode();
  }
}
