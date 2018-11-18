package fr.polytech.tasks;

import com.google.appengine.api.ThreadManager;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class QueueTask extends HttpServlet {

  private List<String> articles = new ArrayList<>();

  public QueueTask(){
    ThreadManager.createBackgroundThread(new TriggerProcess()).start();
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String json = new Gson().toJson(articles);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().write(json);

  }
}
