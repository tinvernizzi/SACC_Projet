/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.polytech;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.LocalDateTime;

public class FileInformationServlet extends HttpServlet {

  private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    FileInformation fileInformation = new FileInformation(req.getParameter("filename"),
        req.getParameter("fileUrl"), Integer.parseInt(req.getParameter("userId")),
        LocalDateTime.now());
    String fileId = insertFileInformation(fileInformation);
    PrintWriter out = resp.getWriter();
    out.println("{\"fileId\": \"" + fileId + "\"" + "}");
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Key key = Key.fromUrlSafe(req.getParameter("fileId"));
    Entity task = datastore.get(key);
    PrintWriter out = resp.getWriter();
    out.println("{\"fileUrl\": \"" + task.getString("fileUrl") + "\"" + "}");
  }

  private String insertFileInformation(FileInformation fileInformation) {
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("fileInformation");

    Key key = keyFactory.newKey(fileInformation.getFileName());
    Entity task = Entity.newBuilder(key)
        .set("filename", fileInformation.getFileName())
        .set("fileUrl", fileInformation.getFileUrl())
        .set("userId", fileInformation.getUserId())
        .set("date", fileInformation.getDateTime().toString())
        .build();
    datastore.put(task);

    return key.toUrlSafe();
  }


}
