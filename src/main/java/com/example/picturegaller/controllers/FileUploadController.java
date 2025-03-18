package com.example.picturegaller.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.example.picturegaller.exceptions.FileOperationException;
import com.example.picturegaller.services.S3Utils;
import com.example.picturegaller.utils.PaginatedResponse;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    @Autowired
    private S3Utils s3;

    @PostMapping("/upload")
    public ResponseEntity<HashMap<String, String>> uploadFile(
            @RequestParam("name") String name, 
            @RequestParam("file") MultipartFile file) {
            // Process the file
            String urlCompatibleName = name.replaceAll(" ", "-");

             HashMap<String, String> response = s3.uploadToS3(file, urlCompatibleName);

             if(response != null)
                return ResponseEntity.ok(response);
            else
                throw new FileOperationException("Cant upload file"); 
            }
   

    //get images
    @GetMapping("/images")
    public PaginatedResponse getImage(@RequestParam(value = "page", required = false) int page,@RequestParam(value = "size", required = false) int size) {
        PaginatedResponse response = s3.getImages(page, size);
        return response;
    }

    @DeleteMapping("/images")
    public ResponseEntity<String> deleteMeassge(@RequestParam(value = "key", required = true) String key) {
        Boolean deleted  = s3.deleteImageFromS3(key);
        if(!deleted)
            throw new FileOperationException("Couldn't Delete File");
        return ResponseEntity.ok("Deleted");
        
    }

}