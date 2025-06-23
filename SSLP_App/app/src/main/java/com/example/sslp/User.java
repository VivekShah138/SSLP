package com.example.sslp;

public class User {
    private String userName,userDOB,userEmail,userBioID,userPassword,userID,userRole;
    User(String userEmail,String userName,String userDOB,String userPassword,String userBioID){
        this.userEmail = userEmail;
        this.userName = userName;
        this.userDOB = userDOB;
        this.userPassword = userPassword;
        this.userBioID = userBioID;
    }
    User(){};

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserBioID() {
        return userBioID;
    }

    public void setUserBioID(String userBioID) {
        this.userBioID = userBioID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
