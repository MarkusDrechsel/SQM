package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.entity.PendingAcountActivation;
import at.ac.tuwien.inso.sqm.entity.UisUserEntity;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserCreationService {

	/**
	 * this method creates a new {@link UisUserEntity} that should not automatically be activated by this method.
	 *
	 * 
	 * Can only be used by ADMINS.
	 * May have extended behavior e.g. sending activation mails.
	 * May throw a runtime exception if any steps fail.
	 * 
	 * @param user should not be null, should have a not empty name, identificationNumber and mail address
	 * @return a {@link PendingAcountActivation} that contains the new {@link UisUserEntity} and an activation id.
	 */
    @PreAuthorize("hasRole('ADMIN')")
    PendingAcountActivation create(UisUserEntity user);
}
