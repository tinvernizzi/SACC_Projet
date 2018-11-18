package fr.polytech;

import static org.apache.http.protocol.HTTP.USER_AGENT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHttp {

  public static String httpPost(String url, String parameters) throws IOException {
    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    //add request header
    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", USER_AGENT);
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

    String urlParameters = parameters;

    // Send post request
    con.setDoOutput(true);
    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.writeBytes(urlParameters);
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

  public static String httpGet(String url, String parameters) throws IOException {
    HttpURLConnection con;
      URL myurl = new URL(url + "?" + parameters);
      con = (HttpURLConnection) myurl.openConnection();

      con.setRequestMethod("GET");

      StringBuilder content;

      try (BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream()))) {

        String line;
        content = new StringBuilder();

        while ((line = in.readLine()) != null) {
          content.append(line);
          content.append(System.lineSeparator());
        }
      }

      return content.toString();
  }

}
