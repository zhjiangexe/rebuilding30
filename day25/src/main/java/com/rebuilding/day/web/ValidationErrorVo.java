package com.rebuilding.day.web;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorVo {

    private final List<FieldErrorVo> fieldErrors = new ArrayList<>();

    public ValidationErrorVo() {
    }

    public void addFieldError(String path, String message) {
        FieldErrorVo error = new FieldErrorVo(path, message);
        fieldErrors.add(error);
    }

    public List<FieldErrorVo> getFieldErrors() {
        return fieldErrors;
    }
}
