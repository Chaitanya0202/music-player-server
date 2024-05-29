package com.restrospare.mongotest.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.restrospare.mongotest.dao.UserDao;
import com.restrospare.mongotest.dto.User;
import com.restrospare.mongotest.repo.UserRepo;

@Service
public class UserService {
    private static final String UPLOAD_DIR = "uploads";

    @Autowired
    private UserDao dao;

    @Autowired
    private UserRepo repo;

    public User saveUser(User user) {
        return dao.saveUser(user);
    }

 public User storeFile(MultipartFile file) throws IOException, Exception, TikaException {
        // Ensure directory exists [if Not then Create that]
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file to the directory
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());

        // Initialize Tika parser [Tika Is Library for Fetch Music data ]
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();

        // Parse the file to extract metadata
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            parser.parse(inputStream, handler, metadata, new ParseContext());
        }

        // Extract image using mp3agic[library used for fetch all]
        byte[] image = extractImage(filePath);

        // Save metadata to the database
        double sizeInMB = file.getSize() / (1024.0 * 1024.0);
        DecimalFormat df = new DecimalFormat("#.##");
        double formattedSize = Double.parseDouble(df.format(sizeInMB));

        // Converted duration from Bytes to minutes and seconds
        String duration = metadata.get("xmpDM:duration");
        String formattedDuration = formatDuration(duration);

        User musicFile = new User();
        musicFile.setFilename(file.getOriginalFilename());
        musicFile.setContentType(file.getContentType());
        musicFile.setSize(formattedSize);
        musicFile.setArtist(metadata.get("xmpDM:artist"));
        musicFile.setAlbum(metadata.get("xmpDM:album"));
        musicFile.setTitle(metadata.get("title"));
        musicFile.setGenre(metadata.get("xmpDM:genre"));
        musicFile.setYear(metadata.get("xmpDM:releaseDate"));
        musicFile.setTrackNumber(metadata.get("xmpDM:trackNumber"));
        musicFile.setComposer(metadata.get("xmpDM:composer"));
        musicFile.setDuration(duration);
        musicFile.setActor(metadata.get("xmpDM:actor"));
        musicFile.setFormattedDuration(formattedDuration);
        musicFile.setImage(image);

        return repo.save(musicFile);
    }
    private String formatDuration(String duration) {
        if (duration != null && !duration.isEmpty()) {
            double durationInSeconds = Double.parseDouble(duration);
            int minutes = (int) durationInSeconds / 60;
            int seconds = (int) durationInSeconds % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
        return "0:00";
    }

    private byte[] extractImage(Path filePath) {
        try {
            Mp3File mp3File = new Mp3File(filePath.toString());
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                byte[] imageData = id3v2Tag.getAlbumImage();
                if (imageData != null) {
                    return imageData;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] getFileContent(String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        return Files.readAllBytes(filePath);
    }
}
