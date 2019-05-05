package at.ac.tuwien.inso.sqm.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import at.ac.tuwien.inso.sqm.entity.PendingAcountActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import at.ac.tuwien.inso.sqm.entity.UisUserEntity;
import at.ac.tuwien.inso.sqm.exception.ValidationException;
import at.ac.tuwien.inso.sqm.repository.PendingAccountActivationRepository;
import at.ac.tuwien.inso.sqm.validator.UisUserValidator;
import at.ac.tuwien.inso.sqm.validator.ValidatorFactory;

@Service
public class UserCreationServiceImpl implements UserCreationService {


	private static final Logger log = LoggerFactory.getLogger(UserCreationServiceImpl.class);
		
    public static final String MAIL_SUBJECT = "user.account.activation.mail.subject";
    private static final String MAIL_TEMPLATE = "emails/user-account-activation-mail";
    private ValidatorFactory validatorFactory = new ValidatorFactory();
    private UisUserValidator validator = validatorFactory.getUisUserValidator();

    @Autowired
    private PendingAccountActivationRepository pendingAccountActivationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private Nachrichten messages;

    @Value("${uis.server.account.activation.url.prefix}")
    private String activationUrlPrefix;

	@Autowired 
	private UisUserServiceInterface userService;
    
    //for doc see interface
    @Transactional
    @Override
    public PendingAcountActivation create(UisUserEntity user) {
    	log.info("creating pendingaccountactivation by user "+user.toString());
   
        validator.validateNewUisUser(user);

        //check if email is unique
        if(userService.existsUserWithMailAddress(user.getEmail())){
        	throw new ValidationException("This is not a valid email. Already exists");
        }
        
        PendingAcountActivation activation = new PendingAcountActivation(user);

        activation = pendingAccountActivationRepository.save(activation);

        log.info("sending activation mail now!");
        mailSender.send(createActivationMail(activation));

        return activation;
    }

    /**
     * creates a activation mail.
     * 
     * may throw a RuntimeException if no MimeMessage can be created
     * 
     * @param activation {@link PendingAcountActivation}  has to contain an id and an UisUserEntity. UisUserEntity should contain an mail address.
     * @return {@link MimeMessage}
     */
    private MimeMessage createActivationMail(PendingAcountActivation activation) throws RuntimeException{
    	log.info("creating activation mail for pending activation");
        MimeMessage mimeMsg = mailSender.createMimeMessage();
        MimeMessageHelper msg = new MimeMessageHelper(mimeMsg, "UTF-8");

        try {
            msg.setTo(activation.getForUser().getEmail());
            msg.setSubject(messages.get(MAIL_SUBJECT));
            msg.setText(messageContent(activation), true);
        } catch (MessagingException e) {
            log.warn("activation message creation FAILED! throwing runtime-exception now");
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.warn("activation message creation FAILED! throwing runtime-exception now");
            throw new RuntimeException(e);
        }

        return msg.getMimeMessage();
    }

    /**
     * Creates a String that represents the message content of the mail.
     * 
     * @param activation {@Link PendingAcountActivation} has to contain an id and an UisUserEntity
     * @return 
     */
    private String messageContent(PendingAcountActivation activation) {
    	log.info("getting message context for PendingAcountActivation "+activation);
        Context context = new Context(Nachrichten.LOCALE);
        context.setVariable("user", activation.getForUser());
        context.setVariable("activationUrl", activationUrlPrefix + activation.getId());
        
        return templateEngine.process(MAIL_TEMPLATE, context);
    }
}
