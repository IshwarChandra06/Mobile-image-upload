package com.eikona.tech.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.eikona.tech.util.ImageProcessingUtil;


@Controller
public class ImageResizeController {
	
	@Autowired
	private ImageProcessingUtil imageProcessingUtil;
	

	@GetMapping("/import/image-directory")
	public String importEmployeeImage() {
		return "multipartfile/uploadImageDirectory";
	}
		
	@PostMapping("/upload/image-directory")
	public String uploadEmployeeImage(@RequestParam("files") MultipartFile[] files) {
		imageProcessingUtil.uploadEmployeeImageDirectory(files);
		return "multipartfile/uploadImageDirectory";
	}
	
}
