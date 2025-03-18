package com.example.picturegaller.exceptions;

public  class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
        
    }

    // Getters and setters

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}


