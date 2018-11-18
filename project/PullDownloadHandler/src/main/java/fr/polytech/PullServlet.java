package fr.polytech;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.gson.Gson;
import fr.polytech.business.DownloadRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class PullServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DownloadRequest downloadRequest = new Gson().fromJson(request.getReader(), DownloadRequest.class);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      ObjectOutput out = new ObjectOutputStream(bos);
      out.writeObject(downloadRequest);
      out.flush();

      Queue queue = QueueFactory.getQueue("queue-pull");

      queue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).payload(new Gson().toJson(downloadRequest)));
    } finally {
      try {
        bos.close();
      } catch (IOException ignored) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      }
    }
  }
}
