package fr.polytech;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

@WebServlet(name = "DownloadServlet")
public class DownloadServlet extends HttpServlet {
    private static ResourceBundle bundle = ResourceBundle.getBundle("configMailer");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String downloadKey = request.getParameter("downloadKey");
        if (isNoob(userId)) {
            Queue queue = QueueFactory.getDefaultQueue();
            String url = bundle.getString("worker.url");
            queue.add(TaskOptions.Builder.withUrl(url + "worker").param("userId", userId).param("downloadKey", downloadKey));
        } else {
            Queue q = QueueFactory.getQueue("pull-queue");
            q.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).param("userId", userId).param("downloadKey", downloadKey));
        }
        response.getWriter().print("Download link is sent by email.");
    }

    private boolean isNoob(String userId) {
        return true;
    }
}
