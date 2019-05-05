package at.ac.tuwien.inso.sqm.controller.student;

import static at.ac.tuwien.inso.sqm.entity.Feedback.Type.DISLIKE;
import static at.ac.tuwien.inso.sqm.entity.Feedback.Type.LIKE;

import java.security.Principal;

import javax.validation.Valid;

import at.ac.tuwien.inso.sqm.entity.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import at.ac.tuwien.inso.sqm.controller.student.forms.FeedbackForm;
import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.service.LehrveranstaltungServiceInterface;
import at.ac.tuwien.inso.sqm.service.FeedbackIService;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;

@Controller
@RequestMapping("/student/feedback")
public class StudentFeedbackController {

    @Autowired
    private StudentServiceInterface studentService;
    @Autowired
    private LehrveranstaltungServiceInterface courseService;
    @Autowired
    private FeedbackIService feedbackService;

    @PostMapping
    public String feedback(@Valid FeedbackForm form,
                           RedirectAttributes redirectAttributes,
                           Principal principal) {
        StudentEntity student = studentService.findByUsername(principal.getName());
        Lehrveranstaltung course = courseService.findeLehrveranstaltung(form.getCourse());

        Feedback feedback = new Feedback(student, course, form.isLike() ? LIKE : DISLIKE, form.getSuggestions());

        feedbackService.save(feedback);

        redirectAttributes.addFlashAttribute("flashMessage", "student.my.courses.feedback.success");
        return "redirect:/student/meineLehrveranstaltungen";
    }
}
