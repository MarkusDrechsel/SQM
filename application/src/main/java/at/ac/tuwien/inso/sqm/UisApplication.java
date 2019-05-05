package at.ac.tuwien.inso.sqm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class UisApplication {

	private static final Logger logger = LoggerFactory.getLogger(UisApplication.class);

	public static void main(String[] args) {
		try
		{
			SpringApplication.run(UisApplication.class, args);
		}
		catch(Error e)
		{
			logger.error("An error occured in the application", e);
			throw e;
		}

	}
}
