package com.judopay.api;

class ApiError {

    private final int code;
    private final String fieldName;
    private final String message;
    private final String detail;

    public ApiError(int code, String fieldName, String message, String detail) {
        this.code = code;
        this.fieldName = fieldName;
        this.message = message;
        this.detail = detail;
    }

    public int getCode() {
        return code;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }
}
