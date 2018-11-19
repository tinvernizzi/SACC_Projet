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

public class InitUserDbServlet extends HttpServlet {

    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("UserInformation");
        Key key = keyFactory.newKey(Long.parseLong(req.getParameter("userId")));

        Entity oldEntityFileInfo = datastore.get(key);

        Entity entityFileInfo = Entity.newBuilder(key).set("userName", oldEntityFileInfo.getString("userName"))
                .set("userEmailAdress", oldEntityFileInfo.getString("userEmailAdress"))
                .set("currentDownloads", oldEntityFileInfo.getString("currentDownloads"))
                .set("timeLastDownload", oldEntityFileInfo.getString("timeLastDownload"))
                .set("userScore", req.getParameter("userScore")).build();

        datastore.put(entityFileInfo);
    }
}
