package com.restrospare.mongotest.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "mydatabase")
public class User {
    @Id
    private String id;
    private String filename;
    private String contentType;
    private double size;
    private String name; // New field for the name of the song
    private String artist;
    private String album;
    private String title;
    private String genre;
    private String year;
    private String trackNumber;
    private String composer;
    private String duration;
    private String formattedDuration;
    private String actor;
    private byte[] image;
    

}
