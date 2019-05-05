package at.ac.tuwien.inso.sqm.controller.lecturer;

import at.ac.tuwien.inso.sqm.dto.*;
import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.exception.*;
import at.ac.tuwien.inso.sqm.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

@Controller
@RequestMapping("/lecturer/course-details")
public class LecturerCourseDetailsController {

    @Autowired
    private LehrveranstaltungServiceInterface courseService;

    @Autowired
    private GradeIService gradeService;

    @Autowired
    private FeedbackIService feedbackService;

    @Autowired
    private Nachrichten messages;

    @GetMapping
    private String getCourseDetails(@RequestParam("courseId") Long courseId, Model model) {
        Lehrveranstaltung course = courseService.findeLehrveranstaltung(courseId);
        model.addAttribute("course", course);
        model.addAttribute("studyPlans", courseService.getSubjectForStudyPlanList(course));
        return "lecturer/course-details";
    }

    @GetMapping("registrations")
    private String getCourseRegistrations(@RequestParam("courseId") Long courseId, Model model) {
        Lehrveranstaltung course = courseService.findeLehrveranstaltung(courseId);
        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());
        return "lecturer/course-registrations";
    }

    @GetMapping("issued-grades")
    private String getIssuedGrades(@RequestParam("courseId") Long courseId, Model model) {
        Lehrveranstaltung course = courseService.findeLehrveranstaltung(courseId);
        model.addAttribute("course", course);
        model.addAttribute("grades", gradeService.getGradesForCourseOfLoggedInLecturer(courseId));
        return "lecturer/issued-grades";
    }

    @GetMapping("feedback")
    private String getCourseFeedback(@RequestParam("courseId") Long courseId, Model model) {
        Lehrveranstaltung course = courseService.findeLehrveranstaltung(courseId);
        model.addAttribute("course", course);
        model.addAttribute("feedbacks", feedbackService.findFeedbackForCourse(courseId));
        return "lecturer/course-feedback";
    }

    @PostMapping("addGrade")
    public String saveGrade(RedirectAttributes redirectAttributes,
                            @RequestParam("courseId") Long courseId,
                            @RequestParam("studentId") Long studentId,
                            @RequestParam("authCode") String authCode,
                            @RequestParam("mark") Integer mark) {

        GradeAuhtorizationDTO dto = gradeService.getDefaultGradeAuthorizationDTOForStudentAndCourse(studentId, courseId);
        dto.getGrade().setMark(MarkEntity.of(mark));
        dto.setAuthCode(authCode);
        StudentEntity student = dto.getGrade().getStudent();
        try {
            gradeService.saveNewGradeForStudentAndCourse(dto);
            String successMsg = messages.msg("lecturer.courses.registrations.issueCertificate.success", student.getName());
            redirectAttributes.addFlashAttribute("flashMessageNotLocalized", successMsg);
        }
        catch (ValidationException e) {
            redirectAttributes.addFlashAttribute("flashMessageNotLocalized", e.getMessage());
            return "redirect:/lecturer/course-details/registrations?courseId=" + courseId;
        }

        return "redirect:/lecturer/course-details/issued-grades?courseId=" + courseId;
    }
}
