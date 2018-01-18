package com.rebuilding.day.web;

public class FieldErrorVo {

    private final String field;
    private final String errorCode;

    public FieldErrorVo(String field, String errorCode) {
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
