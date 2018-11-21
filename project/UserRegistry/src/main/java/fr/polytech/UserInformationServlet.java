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
import org.joda.time.Minutes;

public class UserInformationServlet extends HttpServlet {

  private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    UserInformation UserInformation = new UserInformation(req.getParameter("userName"),
        req.getParameter("userEmailAdress"), 0);
    long userId = insertUserInformation(UserInformation);
    resp.addHeader("Content-Type", "application/json");
    PrintWriter out = resp.getWriter();
    out.println("{\"userId\": \"" + userId + "\"" + "}");
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("UserInformation");
    Key key = keyFactory.newKey(Long.parseLong(req.getParameter("userId")));
    Entity entityFileInfo = datastore.get(key);
    UserInformation userInformation = new UserInformation(entityFileInfo.getString("userName"),
        entityFileInfo.getString("userEmailAdress"), entityFileInfo.getLong("userScore"),
        new LocalDateTime(entityFileInfo.getString("timeLastOperation")),
        entityFileInfo.getLong("currentOperationNumber"));
    resp.addHeader("Content-Type", "application/json");
    PrintWriter out = resp.getWriter();
    out.println("{\"userName\": \"" + entityFileInfo.getString("userName") + "\""
        + ", \"userEmailAdress\" : \""
        + entityFileInfo.getString("userEmailAdress") + "\"" + ", \"userScore\": "
        + entityFileInfo.getLong("userScore") + ", \"canOperate\" : "
        + canOperate(req.getParameter("userId"),
        userInformation)
        + "}");
  }

  private long insertUserInformation(UserInformation UserInformation) {
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("UserInformation");

    Key key = datastore.allocateId(keyFactory.newKey());
    Entity entityFileInfo = Entity.newBuilder(key).set("userName", UserInformation.getUserName())
        .set("userEmailAdress", UserInformation.getUserEmailAdress())
        .set("userScore", UserInformation.getUserScore())
        .set("currentOperationNumber", UserInformation.getCurrentOperationNumber())
        .set("timeLastOperation", UserInformation.getLastOperationTime().toString()).build();
    datastore.put(entityFileInfo);

    return key.getId();
  }

  private boolean canOperate(String userId, UserInformation userInformation) {

    KeyFactory keyFactory = datastore.newKeyFactory().setKind("UserInformation");
    Key key = keyFactory.newKey(Long.parseLong(userId));

    int minutes = Minutes.minutesBetween(userInformation.getLastOperationTime(), LocalDateTime.now()).getMinutes();

    if (userInformation.getUserScore() <= 100) {
      if (minutes < 1) {
        return false;
      } else {
        userInformation.setLastOperationTime(LocalDateTime.now());
        updateLastDownload(userId, userInformation);
      }
    } else if (userInformation.getUserScore() <= 200) {
      if (minutes < 1) {
        if (userInformation.getCurrentOperationNumber() >= 2) {
          return false;
        } else {
          userInformation.setCurrentOperationNumber(userInformation.getCurrentOperationNumber() + 1);
          updateLastDownload(userId, userInformation);
          // currentDowload ++
        }
      } else {
        userInformation.setCurrentOperationNumber(1);
        userInformation.setLastOperationTime(LocalDateTime.now());
        updateLastDownload(userId, userInformation);
        // update time
        // set currentDownload = 1
      }
    } else {
      if (minutes < 1) {
        if (userInformation.getCurrentOperationNumber() >= 4) {
          return false;
        } else {
          userInformation.setCurrentOperationNumber(userInformation.getCurrentOperationNumber() + 1);
          updateLastDownload(userId, userInformation);
          // currentDowload ++
        }
      } else {
        userInformation.setCurrentOperationNumber(1);
        userInformation.setLastOperationTime(LocalDateTime.now());
        updateLastDownload(userId, userInformation);
        // update time
        // set currentDownload = 1
      }
    }
    return true;
  }

  private void updateLastDownload(String userId, UserInformation userInformation) {
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("UserInformation");
    Key key = keyFactory.newKey(Long.parseLong(userId));

    Entity entityFileInfo = Entity.newBuilder(key).set("userName", userInformation.getUserName())
        .set("userEmailAdress", userInformation.getUserEmailAdress())
        .set("userScore", userInformation.getUserScore())
        .set("currentOperationNumber", userInformation.getCurrentOperationNumber())
        .set("timeLastOperation", userInformation.getLastOperationTime().toString()).build();
    datastore.put(entityFileInfo);
  }
}
