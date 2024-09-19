package com.aspire.employee_api_v3.exceptions;

import com.aspire.employee_api_v3.view.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public void handleNumberFormatException(NumberFormatException ex){
        throw new PageNumberException("Invalid Page Number Provided");
    }

    @ExceptionHandler(PageNumberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GenericResponse pageNumberException(PageNumberException ex){
        return new GenericResponse("Invalid Value for Page Number");
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public GenericResponse handleAllOtherException(Exception ex){
        return new GenericResponse("Unknown Error Occurred!");
    }
}
