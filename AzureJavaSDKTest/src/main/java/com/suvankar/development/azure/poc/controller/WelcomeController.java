package com.suvankar.development.azure.poc.controller;

import ch.qos.logback.classic.Logger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.applicationinsights.logback.ApplicationInsightsAppender;
import com.suvankar.development.azure.poc.service.StorageService;
import com.suvankar.development.azure.poc.storage.AzStorageCredential;


@RestController
public class WelcomeController {
	static Logger logger = (Logger) LoggerFactory.getLogger("defaultLogger");
	
	@Autowired
	StorageService storageService;
	
	@Autowired
	AzStorageCredential azCredential;
	
	@RequestMapping("/")
	public String welcome() {
		ApplicationInsightsAppender appender = (ApplicationInsightsAppender) logger.getAppender("aiAppender");

		String configurationKey = appender.getTelemetryClientProxy().getTelemetryClient().getContext().getInstrumentationKey();
		logger.info("Azure Instrumentation Key : " + configurationKey);
		
		logger.info("Got request at root context path.");
		logger.info("Returning response..");
		return "Response from Spring-Boot WelcomeController !! 23-11-17 16:44 HRS";
	}
	
	@PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
		String filePath = request.getServletContext().getRealPath("/"); 
		logger.debug("Root file path : " + filePath);
        try{
        	setAzCredentialFromHeader(request);
			storageService.storeFile(file, filePath);
	        logger.trace("You successfully uploaded " + file.getOriginalFilename() + "!");
	        return "success";
        }catch(Exception ex){
        	logger.error("Exception in upload.");
        	ex.printStackTrace();
        	return "fail|"+ StorageService.getStackTrace(ex);
        }
        
    }
	
	private void setAzCredentialFromHeader(HttpServletRequest request){
		azCredential.setAzStorageAccountName(request.getHeader(azCredential.HEADER_ACCOUNT_NAME));
		azCredential.setAzStorageKey(request.getHeader(azCredential.HEADER_STORAGE_KEY));
		azCredential.setAzSignature(request.getHeader(azCredential.HEADER_STORAGE_SIGNATURE));
		azCredential.setAzShare(request.getHeader(azCredential.HEADER_STORAGE_SHARE));
		azCredential.printCredential();
    }
}
