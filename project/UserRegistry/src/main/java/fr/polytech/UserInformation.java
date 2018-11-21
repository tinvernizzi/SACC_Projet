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

import org.joda.time.LocalDateTime;

public class UserInformation {

  private String userName;
  private String userEmailAdress;
  private long userScore;
  private LocalDateTime lastOperationTime;
  private long currentOperationNumber;

  public UserInformation(String userName, String userEmailAdress, long userScore) {
    this.userName = userName;
    this.userEmailAdress = userEmailAdress;
    this.userScore = userScore;
    this.lastOperationTime = LocalDateTime.now().minusHours(1);
    this.currentOperationNumber = 0;
  }

  public UserInformation(String userName, String userEmailAdress, long userScore,
      LocalDateTime lastDownloadTime, long currentDownloads) {
    this.userName = userName;
    this.userEmailAdress = userEmailAdress;
    this.userScore = userScore;
    this.lastOperationTime = lastDownloadTime;
    this.currentOperationNumber = currentDownloads;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmailAdress() {
    return userEmailAdress;
  }

  public void setUserEmailAdress(String userEmailAdress) {
    this.userEmailAdress = userEmailAdress;
  }

  public long getUserScore() {
    return userScore;
  }

  public void setUserScore(long userScore) {
    this.userScore = userScore;
  }

  public LocalDateTime getLastOperationTime() {
    return this.lastOperationTime;
  }

  public void setLastOperationTime(LocalDateTime lastOperationTime) {
    this.lastOperationTime = lastOperationTime;
  }

  public long getCurrentOperationNumber() {
    return this.currentOperationNumber;
  }

  public void setCurrentOperationNumber(long currentOperationNumber) {
    this.currentOperationNumber = currentOperationNumber;
  }
}
