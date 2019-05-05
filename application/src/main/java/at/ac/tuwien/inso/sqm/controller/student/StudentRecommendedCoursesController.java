package at.ac.tuwien.inso.sqm.controller.student;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.service.LehrveranstaltungServiceInterface;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;
import at.ac.tuwien.inso.sqm.service.course_recommendation.RecommendationIService;

@Controller
@RequestMapping("/student/recommended")
public class StudentRecommendedCoursesController {

    private static final Logger log = LoggerFactory.getLogger(StudentMyCoursesController.class);


    @Autowired
    private StudentServiceInterface studentService;

    @Autowired
    private LehrveranstaltungServiceInterface courseService;

    @Autowired
    private RecommendationIService recommendationService;



    @ModelAttribute("recommendedCourses")
    private List<Lehrveranstaltung> getRecommendedCourses(Principal principal) {

        log.info("Getting recommendation for student: [{}]", principal.getName());

        StudentEntity student = getLoggedInStudent(principal);
        return recommendationService.recommendCoursesSublist(student);
    }


    @GetMapping
    public String courses() {
        return "/student/recommended";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String dismissCourse(Principal principal, Long dismissedId) {
        log.info("Post with [{}] as request body", dismissedId);
        StudentEntity student = getLoggedInStudent(principal);
        courseService.dismissCourse(student, dismissedId);
        return "redirect:/student/recommended";
    }


    private StudentEntity getLoggedInStudent(Principal principal) {
        StudentEntity student = studentService.findByUsername(principal.getName());
        return student;
    }

}
