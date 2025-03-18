package com.example.picturegaller.exceptions;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.io.IOException;

import com.example.picturegaller.exceptions.ErrorResponse;
import com.example.picturegaller.exceptions.FileOperationException;

@RestControllerAdvice
public class ExceptionAdvicer {

    // Handle specific exceptions (e.g., IOException)
    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, WebRequest request) {
        // Log exception (if needed)
        // log.error("IOException occurred: ", ex);

        // Build error response
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        
        // Return a ResponseEntity with a 500 status
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation exceptions (e.g., for invalid input)
    

    // Global exception handler for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    

        // Build a generic error response
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        
        // Return a ResponseEntity with a 500 status
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ErrorResponse> fileSizeExceed(Exception ex, WebRequest request) {
    

            // Build a generic error response
            ErrorResponse errorResponse = new ErrorResponse("File Size Exceede, File must be less than 5 gig");
            
            // Return a ResponseEntity with a 500 status
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
    // Custom error response class
}