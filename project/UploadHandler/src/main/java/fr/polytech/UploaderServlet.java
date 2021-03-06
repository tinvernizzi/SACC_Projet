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

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static fr.polytech.RequestHttp.httpGet;
import static fr.polytech.RequestHttp.httpPost;

// [START example]
@SuppressWarnings("serial")
public class UploaderServlet extends HttpServlet {

  private static ResourceBundle bundle = ResourceBundle.getBundle("configUploadHandler");

  private Storage storage = StorageOptions.getDefaultInstance().getService();
  private String bucketName = bundle.getString("fileBucket.name");
  private String URL_USER_REGISTRY = bundle.getString("userRegistry.url");


  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (req.getParameter("userId") == null) {
      PrintWriter out = resp.getWriter();
      out.println("wrong parameters");
      return;
    }
    PrintWriter out = resp.getWriter();
    long userId = Long.parseLong(req.getParameter("userId"));
    JSONObject userInfoJson = getUserInfo(userId);
    if(userInfoJson == null) {
      out.println("user unknown");
    }
    if (!isAuthorizedToUpload(userInfoJson)) {
      out.println("You are not authorized to upload.");
      return;
    }

    FileItemStream file = null;
    try {
      ServletFileUpload upload = new ServletFileUpload();
      FileItemIterator iter = upload.getItemIterator(req);
      file = iter.next();
    } catch (FileUploadException e) {
      out.println("Upload Failed.");
      return;
    }

    String link = uploadFile(file);

    String fileId = addFileInformation(file.getName(), link,
        userId);

    sendMail(getUserEmail(userInfoJson), fileId);

    out.println("upload ok !");
  }

  private boolean isAuthorizedToUpload(JSONObject userInfoJson) {
      return userInfoJson.getBoolean("canOperate");
  }

  private JSONObject getUserInfo(long userId) {
    try {
      String userInfo = httpGet(URL_USER_REGISTRY, "userId=" + userId);
      return new JSONObject(userInfo);
    } catch (IOException e) {
      return null;
    }
  }

  private String getUserEmail(JSONObject userInfoJson) {
    return userInfoJson.getString("userEmailAdress");
  }

  private String uploadFile(FileItemStream file) throws IOException {

    InputStream fileStream = file.openStream();

    String fileName = file.getName();

    // the inputstream is closed by default, so we don't need to close it here
    BlobInfo blobInfo =
        storage.create(
            BlobInfo
                .newBuilder(bucketName, fileName)
                // Modify access list to allow all users with link to read file
                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
                .build(),
            fileStream);
    // return the public download link

    return blobInfo.getMediaLink();
  }

  private String addFileInformation(String fileName, String fileUrl, long userId)
      throws IOException {
    String url = bundle.getString("fileRegistry.url");
    String urlParameters = "filename=" + fileName + "&userId=" + userId + "&fileUrl=" + fileUrl;
    String response = httpPost(url, urlParameters);

    JSONObject jsonResponse = new JSONObject(response.toString());
    return jsonResponse.getString("fileId");
  }

  private void sendMail(String address, String fileId) throws IOException {
    String subject = "File uploaded";
    String content = "Your file has been uploaded. the fileId is: " + fileId;
    String url = bundle.getString("mailer.url");
    String urlParameters = "address=" + address + "&subject=" + subject + "&content=" + content;
    httpPost(url, urlParameters);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter out = resp.getWriter();
    out.println("this url allows to request file");
  }
}
// [END example]
