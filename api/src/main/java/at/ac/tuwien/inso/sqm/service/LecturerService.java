package at.ac.tuwien.inso.sqm.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LecturerService {

	/**
	 * gibt den aktuellen eingeloggten Vortragenden zurück
     *
	 * Benutzer muss ein Vortragender sein
	 * 
	 * @return der eingeloggte Vortragende
	 */
    @PreAuthorize("hasRole('LECTURER')")
    LecturerEntity getLoggedInLecturer();

    /**
     * gibt alle Fächer, welche zum aktuellen eingeloggten Vortragenden gehören zurück
     *
     * Benutzer muss ein Vortragender sein
     * 
     * @return
     */
    @PreAuthorize("hasRole('LECTURER')")
    Iterable<Subjcet> getOwnSubjects();

    /**
     * findet Fächer für einen angegebenen Vortragenden. Vortragender sollte nicht null sein
     *
     * Benutzer muss authentifiziert werden
     * 
     * @param lecturer
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<Subjcet> findSubjectsFor(LecturerEntity lecturer);

    /**
     * generiert die Url für den QR Code
     * 
     * @param lecturer, id, email und Zwei-Faktor-Authentifizierung sollte nicht null sein
     * @return die Url als String
     * @throws UnsupportedEncodingException
     */
    @PreAuthorize("hasRole('ADMIN')")
    String generateQRUrl(LecturerEntity lecturer) throws UnsupportedEncodingException;
}
