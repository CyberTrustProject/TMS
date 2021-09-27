package com.cybertrust.tms.exceptionhandling;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        String message = "";
        
        if(errors.size() == 1)
        	message = errors.get(0);
        else if (errors.size() > 1){
        	for(String error : errors) {
        		message = message + error + "\n";
        	}
        }
        	
        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        
        String message = "";
        
        if(errors.size() == 1)
        	message = errors.get(0);
        else if (errors.size() > 1){
        	for(String error : errors) {
        		message = message + error + "\n";
        	}
        }
        
        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        
        final String message = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        
        final String message = ex.getRequestPartName() + " part is missing";
        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        
        final String message = ex.getParameterName() + " parameter is missing";
        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
    	logger.info(ex.getClass().getName());
    	
        final String message = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }
        
        String message = "";
        
        if(errors.size() == 1)
        	message = errors.get(0);
        else if (errors.size() > 1){
        	for(String error : errors) {
        		message = message + error + "\n";
        	}
        }

        final ApiError apiError = new ApiError(System.currentTimeMillis(), HttpStatus.BAD_REQUEST.value(), "Bad Request", 
        		message, request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
