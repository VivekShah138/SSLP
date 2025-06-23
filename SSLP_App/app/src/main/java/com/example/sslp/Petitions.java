package com.example.sslp;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Petitions implements Serializable {

    private String petitionId_; // petitionId
    private String title_; // Title Of Petition
    private String details_; // Details of Petition
    private String userId_; // Petitioner User Id

    private String petitionerName_;  // petition Creator Name
    private List<String>signature_; // Saves the userId of all those who signed petitions
    private boolean isClosed_; // is Petition close or not
    private String status_;  // Status of Petition

    private Integer thresholdSignatures_; // threshold number of signatures required to reach the parliament

    private String response_;


    public Petitions(String title, String details, String userId,List<String>signature){
        this.title_ = title;
        this.details_ = details;
        this.userId_ = userId;
        signature_ = signature;
        this.isClosed_ = false;
        setStatus_();
    }

    public Petitions(){}

    @PropertyName("title_")
    public String getTitle_() {
        return title_;
    }

    @PropertyName("title_")
    public void setTitle_(String title_) {
        this.title_ = title_;
    }

    @PropertyName("details_")
    public String getDetails_() {
        return details_;
    }

    @PropertyName("details_")
    public void setDetails_(String details_) {
        this.details_ = details_;
    }

    @PropertyName("userId_")
    public String getUserId() {
        return userId_;
    }

    @PropertyName("userId_")
    public void setUserId(String userId) {
        this.userId_ = userId;
    }

    @PropertyName("petitionId_")
    public String getPetitionId_() {
        return petitionId_;
    }

    @PropertyName("petitionId_")
    public void setPetitionId_(String petitionId_) {
        this.petitionId_ = petitionId_;
    }

    @PropertyName("signature_")
    public List<String> getSignature_(){
        return signature_;
    }


    @PropertyName("signature_")
    public void setSignature_(List<String>signature){
        this.signature_ = signature;
    }


    @PropertyName("isClosed_")
    public boolean getIsClosed() {
        return isClosed_;
    }

    @PropertyName("isClosed_")
    public void setClosed(boolean closed) {
        isClosed_= closed;
    }

    @PropertyName("status_")
    public String getStatus_() {
        return status_;
    }

    @PropertyName("status_")
    public void setStatus_() {
        if(getIsClosed() == true) {
            this.status_ = "CLOSED";
        }
        else{
            this.status_ = "OPEN";
        }
    }

    @PropertyName("thresholdSignatures_")
    public Integer getThresholdSignatures_() {
        return thresholdSignatures_;
    }

    @PropertyName("thresholdSignatures_")
    public void setThresholdSignatures_(Integer thresholdSignatures_) {
        this.thresholdSignatures_ = thresholdSignatures_;
    }

    @PropertyName("response_")
    public String getResponse_() {
        return response_;
    }

    @PropertyName("response_")
    public void setResponse_(String response_) {
        this.response_ = response_;
    }


    @Exclude
    public int getSignatureSize_() {
        if(signature_ == null){
            return 0;
        }
        return signature_.size();
    }

    public void addSignature(String userId) {
        if(signature_ == null){
            signature_ = new ArrayList<>();
        }
        this.signature_.add(userId);
    }
}
