package at.ac.tuwien.inso.sqm.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;

public interface SemesterServiceInterface {

    /**
     * Erstellt ein neues Semester.
     *
     * Benutzer muss als Admin authentifiziert werden.
     *
     * @param semester das neue zu erstellende Semester
     * @return das erstellte Semester
     */
    @PreAuthorize("hasRole('ADMIN')")
    SemesterDto create(SemesterDto semester);

    /**
     * Gibt das aktuelle Semester zurück. Nur Integrationstests in Tests verwenden!
     *
     * Immer getOrCreateCurrentSemester verwenden:
     * Integrationtest prüft auch, ob ein neues Semester gestartet werden kann und startet Integrationstest automatisch.
     */
    @PreAuthorize("isAuthenticated()")
    SemesterDto getCurrentSemester();

    /**
     * Gibt das aktuelle Semester zurück.
     *
     * Wenn ein neues Semester gestartet werden kann, wird das neue Semester gestartet und zurückgegeben.
     */
    @PreAuthorize("isAuthenticated()")
    SemesterDto getOrCreateCurrentSemester();

    /**
     * Sollte alle Semester als SemesterDto zurückgeben. Diese sind nach ihrer id absteigend sortiert.
     *
     * Benutzer muss authentifiziert werden.
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<SemesterDto> findAll();

    /**
     * Gibt alle Semester seit dem übergebenen Semester zurück.
     * 
     * Benutzer muss authentifiziert werden.
     * @param semester
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    List<SemesterDto> findAllSince(SemesterDto semester);
}
