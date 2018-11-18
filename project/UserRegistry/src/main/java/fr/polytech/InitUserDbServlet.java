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
        UserInformation userInformation = new UserInformation("Tanguy Invernizzi", "ti@unice.fr", 0);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Mec Hyper Cool", "mhc@gmail.fr", 20);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Mec Giga Cool", "abc@unice.fr", 40);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Sebi", "super_giga_codeur_du_06@unice.fr", 60);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Enzo", "mega_codeur_du_74@unice.fr", 80);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Shiyang", "cool_codeur_de_chine@unice.fr", 100);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Fabrice", "prof_cloud@unice.fr", 120);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Sebastien", "prof_SOA@unice.fr", 140);
        insertUserInformation(userInformation);

        userInformation = new UserInformation("Molines", "prof_AL@unice.fr", 160);
        insertUserInformation(userInformation);
    }

    private long insertUserInformation(UserInformation UserInformation) {
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("UserInformation");

        Key key = datastore.allocateId(keyFactory.newKey());
        Entity entityFileInfo = Entity.newBuilder(key).set("userName", UserInformation.getUserName())
                .set("userEmailAdress", UserInformation.getUserEmailAdress())
                .set("userScore", UserInformation.getUserScore()).build();
        datastore.put(entityFileInfo);

        return key.getId();
    }
}
