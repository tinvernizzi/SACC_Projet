package fr.polytech.tasks;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.polytech.business.DownloadRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class TriggerProcess implements Runnable {

    private static final String USER_AGENT = "Mozilla/5.0";

    private static ResourceBundle bundle = ResourceBundle.getBundle("configPullHandler");

    private static final String URL_FILE = bundle.getString("fileRegistry.url");

    private static final String URL_MAIL = bundle.getString("mailer.url");

    private static final String URL_USER = bundle.getString("userRegistry.url");

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                this.leaseTasks();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
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

    private void processRequest(DownloadRequest request){
        try {
            JsonObject user = getUserFromGET(request.getUserId());

            if(user.get("canOperate").getAsBoolean()) {
                //Get from file registry
                String link;

                link = sendGET(request.getFileId());

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

            return (JsonObject) new JsonParser().parse(response.toString());
        } else {
            System.out.println("GET request not worked");
        }

        return new JsonObject();
    }

    private void leaseTasks() {
        Queue queue = QueueFactory.getQueue("queue-pull");

        List<TaskHandle> tasks = queue.leaseTasks(1, TimeUnit.SECONDS, 1);

        processTasks(tasks, queue);
    }

    private void processTasks(List<TaskHandle> tasks, Queue queue) {
        String payload;

        for (TaskHandle task : tasks) {
            payload = new String(task.getPayload());

            DownloadRequest downloadRequest = new Gson().fromJson(payload, DownloadRequest.class);

            this.processRequest(downloadRequest);

            queue.deleteTask(task);
        }
    }
}
