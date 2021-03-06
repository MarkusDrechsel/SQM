package at.ac.tuwien.inso.sqm.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Nachrichten {
	
	private static final Logger log = LoggerFactory.getLogger(Nachrichten.class);

    public static final Locale LOCALE = Locale.ENGLISH;

    @Autowired
    private MessageSource messageSource;

    public String get(String path) {
    	log.info("Nachrichten für den Pfad "+path+" erhalten.");
        return messageSource.getMessage(path, null, LOCALE);
    }

    public String msg(String path, Object... args) {
    	log.info("Nachrichten für Objekt und Pfad "+path+" erhalten.");
        return messageSource.getMessage(path, args, LocaleContextHolder.getLocale());
    }
}
