package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.entity.PendingAcountActivation;
import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;

public interface AccountActivationServiceInterface {

	/**
	 * tries to return the PendingAcountActivation Object that is linked to the parameter activationCode. If there is no object found a BusinessObjectNotFoundException will be thrown.
	 * @param activationCode
	 * @return a found PendingAcountActivation object or a BusinessObjectNotFoundException
	 */
    PendingAcountActivation findOne(String activationCode);

    /**
     * 
     * activates the account that is identified by activationCode and sets the UserAccountEntity to integrationtest.
     * 
     * @param activationCode a previously created activation code
     * @param account an account to activate with the activation code
     */
    void activateAccount(String activationCode, UserAccountEntity account);
}
