package at.ac.tuwien.inso.sqm.controller.student;

import java.security.Principal;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.service.LehrveranstaltungServiceInterface;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;
import at.ac.tuwien.inso.sqm.service.Nachrichten;

@Controller
@RequestMapping("/student")
public class StudentLehrveranstaltungRegistrierungsController {

    @Autowired
    private LehrveranstaltungServiceInterface lehrveranstaltungService;

    @Autowired
    private StudentServiceInterface studentService;

    @Autowired
    private Nachrichten nachrichten;

    @PostMapping("/anmelden/{lehrveranstaltungsID}")
    public String studentAnmelden(
            @PathVariable Long lehrveranstaltungsID,
            RedirectAttributes redirectAttributes
    ) {
        Lehrveranstaltung lehrveranstaltung = lehrveranstaltungService.findeLehrveranstaltung(lehrveranstaltungsID);

        if (lehrveranstaltungService.studentZurLehrveranstaltungAnmelden(lehrveranstaltung)) {
            String lehrveranstaltungsName = lehrveranstaltung.getSubject().getName();
            String erfolgreicheNachricht = nachrichten.msg("student.meine.lehrveranstaltungen.anmeldung.erfolgreich", lehrveranstaltungsName);
            redirectAttributes.addFlashAttribute("flashMessageNotLocalized", erfolgreicheNachricht);
            return "redirect:/student/lehrveranstaltungen";
        }

        String fehlgeschlageneNachricht = nachrichten.msg("student.meine.lehrveranstaltungen.anmeldung.fehlgeschlagen", lehrveranstaltung.getSubject().getName());
        redirectAttributes.addFlashAttribute("flashMessageNotLocalized", fehlgeschlageneNachricht);
        return "redirect:/student/lehrveranstaltungen";
    }

    @PostMapping("/abmelden")
    public String studentAbmelden(@RequestParam Long course,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal) {
        StudentEntity student = studentService.findByUsername(principal.getName());
        Lehrveranstaltung zuAbmeldendeLehrveranstaltung = lehrveranstaltungService.studentVonLehrveranstaltungAbmelden(student, course);

        String erfolgreicheNachricht = nachrichten.msg("student.meine.lehrveranstaltungen.abmeldung.erfolgreich", zuAbmeldendeLehrveranstaltung.getSubject().getName());
        redirectAttributes.addFlashAttribute("flashMessageNotLocalized", erfolgreicheNachricht);
        return "redirect:/student/meineLehrveranstaltungen";
    }
}
