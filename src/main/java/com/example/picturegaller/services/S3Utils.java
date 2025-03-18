package com.example.picturegaller.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3Object;
import com.example.picturegaller.utils.PaginatedResponse;

import lombok.Getter;
import lombok.Setter;

import com.amazonaws.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Getter
@Setter
public class S3Utils {
    
    @Value("${s3.bucketName}")
    private String bucketName;


    private  AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .build();
    }


    public HashMap<String,String> uploadToS3(MultipartFile file, String fileName){
        try {
    
        HashMap<String, String> response = new HashMap<>();
        AmazonS3 s3 = this.s3Client();
        File uploadedFile = convertMultipartFileToFile(file);
        String key = this.generateS3UniqueKey(fileName);
        s3.putObject(this.bucketName, key, uploadedFile);
        String url = this.generatePresignedUrl(key, (long) 60*60*24);
        response.put("key", key);
        response.put("url", url);
        return  response;
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        
    }


    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // Create a temporary file in the system's temporary directory
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());

        // Transfer the content of MultipartFile to the temporary file
        multipartFile.transferTo(file);

        return file;
    }

     public  String generateS3UniqueKey(String prefix) {
        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Generate a unique UUID
        String uuid = UUID.randomUUID().toString();

        // Combine prefix, timestamp, and UUID to create a unique key
        return prefix + "/" + timestamp + "-" + uuid;
    }


    private String generatePresignedUrl(String fileName, long expirationInMillis) {
        // Set the expiration time for the URL (e.g., 1 hour)
        Date expiration = new Date(System.currentTimeMillis() + expirationInMillis);

        // Generate the pre-signed URL request
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(this.bucketName, fileName)
                .withMethod(HttpMethod.GET) // HTTP method (GET to download the file)
                .withExpiration(expiration); // URL expiration time

        // Generate the pre-signed URL
        URL url = this.s3Client().generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString(); // Return the URL as a string
    }

    public  PaginatedResponse getImages(int pageNumber, int size){
        var response = new ArrayList<HashMap<String, String>>();
        int currentPage = 0;
        String continuationToken = null;
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(5);
        ListObjectsV2Result results = null;

        do {
        if(continuationToken != null && !continuationToken.isEmpty())
            request.withContinuationToken(continuationToken);

         results = this.s3Client().listObjectsV2(request);
         currentPage += 1;
         continuationToken =  results.getNextContinuationToken();

        } while(currentPage < pageNumber && continuationToken != null);

        PaginatedResponse page = new PaginatedResponse();
        if(currentPage < pageNumber && continuationToken == null) {
            page.setData(null);
            page.setLast(true);
        } else {
            List<S3ObjectSummary> objects = results.getObjectSummaries();
        for (S3ObjectSummary object : objects) {
            var bucketData = new HashMap<String, String>();
            String url  = this.generatePresignedUrl(object.getKey(), (long) 60*60*24);
            bucketData.put("url", url);
            bucketData.put("key", object.getKey());
            response.add(bucketData);
        }
        page.setData(response);
        page.setLast(continuationToken == null);
        }
        page.setPageNumber(pageNumber);
        return page;

    }

     public Boolean deleteImageFromS3(String key) {
        try {
            // Create delete request with the object key
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
            
            // Delete the object from S3
            s3Client().deleteObject(deleteObjectRequest);
            
           return true;
        } catch (Exception e) {
            System.err.println("Error deleting image from S3: " + e.getMessage());
            return false;
        }
    }
}