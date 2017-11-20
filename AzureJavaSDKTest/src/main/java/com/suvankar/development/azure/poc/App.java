package com.suvankar.development.azure.poc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = { "com.suvankar.development.azure.poc.*" })
@SpringBootApplication
public class App extends SpringBootServletInitializer {
	static Logger logger = LoggerFactory.getLogger("defaultLogger");
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(App.class);
	}

	public static void main(String[] args) throws Exception {
		logger.debug("########## Booting up application ##########");
		SpringApplication.run(App.class, args);
	}
}
