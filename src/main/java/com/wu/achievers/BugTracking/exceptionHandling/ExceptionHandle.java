package com.wu.achievers.BugTracking.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(NotFoundException enf){
        ErrorResponse responseObj=new ErrorResponse(HttpStatus.NOT_FOUND.value(),enf.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<ErrorResponse> (responseObj,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(BadRequestException e){
        ErrorResponse responseObj=new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<ErrorResponse> (responseObj,HttpStatus.BAD_REQUEST);
    }
}
