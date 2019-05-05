package at.ac.tuwien.inso.sqm.controller.admin;

import java.util.List;

import javax.validation.Valid;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import at.ac.tuwien.inso.sqm.controller.admin.forms.AddLecturersToSubjectForm;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.exception.LecturerNotFoundException;
import at.ac.tuwien.inso.sqm.exception.RelationNotfoundException;
import at.ac.tuwien.inso.sqm.exception.SubjectNotFoundException;
import at.ac.tuwien.inso.sqm.service.SubjectIService;

@Controller
@RequestMapping("/admin/subjects/{subjectId}")
public class AdminSubjectLecturersController {

    @Autowired
    private SubjectIService subjectService;

    @ModelAttribute("subject")
    private Subjcet getSubject(@PathVariable Long subjectId) {
        return subjectService.findOne(subjectId);
    }

    @GetMapping(value = "/availableLecturers.json")
    @ResponseBody
    public List<LecturerEntity> getAvailableLecturers(
            @PathVariable Long subjectId,
            @RequestParam(value = "search", defaultValue = "") String search
    ) {
        return subjectService.getAvailableLecturersForSubject(subjectId, search);
    }

    @PostMapping("/lecturers")
    public String addLecturer(
            @PathVariable Long subjectId,
            @Valid AddLecturersToSubjectForm addLecturersToSubjectForm,
            RedirectAttributes redirectAttributes
    ) {

        try {
            Long lecturerUisUserId = addLecturersToSubjectForm.toLecturerId();
            LecturerEntity lecturer = subjectService.addLecturerToSubject(subjectId, lecturerUisUserId);
            String name = lecturer.getName();
            String msg = String.format("admin.subjects.lecturerAdded(${'%s'})", name);
            redirectAttributes.addFlashAttribute("flashMessage", msg);
        }
        catch (LecturerNotFoundException e) {
            redirectAttributes.addFlashAttribute("flashMessageNotLocalized", e.getMessage());
        }

        return "redirect:/admin/subjects/" + subjectId;
    }

    @PostMapping("/lecturers/{lecturerId}/delete")
    public String removeLecturer(
            @PathVariable Long subjectId,
            @PathVariable Long lecturerId,
            RedirectAttributes redirectAttributes
    ) {

        try {
            LecturerEntity removed = subjectService.removeLecturerFromSubject(subjectId, lecturerId);

            String name = removed.getName();
            String msg = String.format("admin.subjects.lecturer.removed(${'%s'})", name);
            redirectAttributes.addFlashAttribute("flashMessage", msg);

            return "redirect:/admin/subjects/" + subjectId;

        } catch (SubjectNotFoundException e) {
            String msgId = "admin.subjects.lecturer.subjectNotFound";
            redirectAttributes.addFlashAttribute("flashMessage", msgId);

            return "redirect:/admin/subjects";

        } catch (LecturerNotFoundException e) {
            String msgId = "admin.subjects.lecturer.lecturerNotFound";
            redirectAttributes.addFlashAttribute("flashMessage", msgId);

            return "redirect:/admin/subjects/" + subjectId;

        } catch (RelationNotfoundException e) {
            String msgId = "admin.subjects.lecturer.wasNoLecturer";
            redirectAttributes.addFlashAttribute("flashMessage", msgId);

            return "redirect:/admin/subjects/" + subjectId;
        }
    }

}
