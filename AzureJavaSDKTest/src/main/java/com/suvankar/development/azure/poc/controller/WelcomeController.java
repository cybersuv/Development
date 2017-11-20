package com.suvankar.development.azure.poc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
	static Logger logger = LoggerFactory.getLogger("defaultLogger");
	@RequestMapping("/")
	public String welcome() {
		logger.debug("Got request at root context path.");
		logger.debug("Returning response..");
		return "Response from Spring-Boot WelcomeController !!";
	}
}
