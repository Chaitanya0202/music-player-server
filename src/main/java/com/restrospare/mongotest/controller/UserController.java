package com.restrospare.mongotest.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.restrospare.mongotest.dto.User;
import com.restrospare.mongotest.repo.UserRepo;
import com.restrospare.mongotest.service.UserService;

@RestController
@CrossOrigin("http://localhost:3000/")
public class UserController {

	@Autowired
	private UserRepo repo;
	@Autowired
	private UserService service;

//	music 
	@PostMapping("/uploadMusic")
	public ResponseEntity<User> uploadFile(@RequestParam("file") MultipartFile file) throws Exception, Exception {
		User musicFile = service.storeFile(file);
		return new ResponseEntity<>(musicFile, HttpStatus.OK);
	}

	@GetMapping("/musicFiles")
	public List<User> getAllMusicFiles() {
		return repo.findAll();
	}

	@GetMapping("/musicFiles/{id}")
	public ResponseEntity<byte[]> getMusicFile(@PathVariable String id) throws IOException {
		User musicFile = repo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
		byte[] fileContent = service.getFileContent(musicFile.getFilename());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(musicFile.getContentType())).body(fileContent);
	}

	@GetMapping("/musicFiles/{id}/image")
	public ResponseEntity<byte[]> getMusicFileImage(@PathVariable String id) {
		User musicFile = repo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
		byte[] imageContent = musicFile.getImage();
		if (imageContent != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageContent);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@GetMapping("/musicFiles/{id}/file")
	public ResponseEntity<byte[]> getMusicFileContent(@PathVariable String id) throws IOException {
		User musicFile = repo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
		byte[] fileContent = service.getFileContent(musicFile.getFilename());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(musicFile.getContentType())).body(fileContent);
	}

}
