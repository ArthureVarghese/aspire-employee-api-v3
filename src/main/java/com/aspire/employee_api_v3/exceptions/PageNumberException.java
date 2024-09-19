package com.aspire.employee_api_v3.exceptions;

public class PageNumberException extends RuntimeException{
    public PageNumberException(String message) {
        super("Invalid Value detected : " + message);
    }
}
