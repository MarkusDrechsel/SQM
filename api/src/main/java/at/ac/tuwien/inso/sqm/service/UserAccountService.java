package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAccountService extends UserDetailsService {

	/**
     * kann nur von authentifizierten Benutzern verwendet werden. Diese Methode gibt die aktuell angemeldeten {@link UserAccountEntity} zurück
	 * @return
	 */
    @PreAuthorize("isAuthenticated()")
    UserAccountEntity getCurrentLoggedInUser();

    /**
     * gibt true zurück, falls der Benutzername existiert. ansonsten false.
     * @param username
     * @return den boolean Wert true, falls der Benutzername existiert.
     */
    boolean existsUsername(String username);
}
