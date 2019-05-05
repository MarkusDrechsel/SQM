package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.entity.PendingAcountActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import at.ac.tuwien.inso.sqm.exception.BusinessObjectNotFoundException;
import at.ac.tuwien.inso.sqm.repository.PendingAccountActivationRepository;

@Service
public class AccuontActivationServiceImpl implements AccountActivationServiceInterface {

	private static final Logger log = LoggerFactory.getLogger(AccuontActivationServiceImpl.class);
	
    @Autowired
    private PendingAccountActivationRepository pendingAccountActivationRepository;

    @Autowired
    private Nachrichten messages;

    @Override
    @Transactional(readOnly = true)
    public PendingAcountActivation findOne(String activationCode) {
    	log.info("try finding pending activation for activationcode "+activationCode);
        PendingAcountActivation pendingAccountActivation = pendingAccountActivationRepository.findOne(activationCode);

        if (pendingAccountActivation == null) {
        	log.warn("failed finding pending activaiton for activation code "+activationCode);
            throw new BusinessObjectNotFoundException("Account activation not found");
        }

        return pendingAccountActivation;
    }

    @Override
    @Transactional
    public void activateAccount(String activationCode, UserAccountEntity account) {
    	log.info("activating account with activationCode "+activationCode+" for account "+account.toString());
        PendingAcountActivation pendingAccountActivation = findOne(activationCode);

        pendingAccountActivation.getForUser().activate(account);

        pendingAccountActivationRepository.delete(pendingAccountActivation);
    }
}
