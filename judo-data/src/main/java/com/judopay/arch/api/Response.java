package com.judopay.arch.api;

public class Response {

    protected String result;

    public boolean isSuccess() {
        return "Success".equals(result);
    }

    public boolean isDeclined() {
        return "Declined".equals(result);
    }

    public String getResult() {
        return result;
    }

}