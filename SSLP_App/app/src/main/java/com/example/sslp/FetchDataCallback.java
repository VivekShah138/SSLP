package com.example.sslp;

import java.util.List;

public interface FetchDataCallback {

    void onSuccess(List<String> bioIDs);
    void onError(String errorMessage);
}

