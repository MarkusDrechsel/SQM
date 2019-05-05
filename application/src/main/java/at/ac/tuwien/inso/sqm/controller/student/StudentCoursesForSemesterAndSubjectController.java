package at.ac.tuwien.inso.sqm.controller.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.service.LehrveranstaltungServiceInterface;
import at.ac.tuwien.inso.sqm.service.SubjectIService;

@Controller
@RequestMapping("/student/lehrveranstaltungen/semester/subject")
public class StudentCoursesForSemesterAndSubjectController {

    private static final Logger log = LoggerFactory.getLogger(StudentCoursesForSemesterAndSubjectController.class);

    @Autowired
    private LehrveranstaltungServiceInterface courseService;

    @Autowired
    private SubjectIService subjectService;

    @GetMapping
    public String courses(@RequestParam Long subjectId, Model model) {

        log.info("getting courses for subject " + subjectId);

        Subjcet subject = subjectService.findOne(subjectId);
        List<Lehrveranstaltung> courses = courseService.findCoursesForSubjectAndCurrentSemester(subject);

        if (courses.size() == 1) {
            log.info("Subjcet only has one course this semester. Redirecting to course");
            return "redirect:/student/lehrveranstaltungen/" + courses.get(0).getId();
        }

        model.addAttribute("coursesForSemesterAndSubject", courses);
        return "/student/courses-for-subject";
    }


}
