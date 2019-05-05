package at.ac.tuwien.inso.sqm.controller.student;

import java.security.Principal;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.ac.tuwien.inso.sqm.controller.Constants;
import at.ac.tuwien.inso.sqm.entity.Lehrveranstaltung;
import at.ac.tuwien.inso.sqm.service.LehrveranstaltungServiceInterface;
import at.ac.tuwien.inso.sqm.service.StudentServiceInterface;

@Controller
@RequestMapping("/student/lehrveranstaltungen")
public class StudentCoursesController {

    private static final Logger log = LoggerFactory.getLogger(StudentMyCoursesController.class);

    @Autowired
    private LehrveranstaltungServiceInterface courseService;

    @Autowired
    private StudentServiceInterface studentService;

    @ModelAttribute("allCourses")
    private Page<Lehrveranstaltung> getAllCourses(@RequestParam(value = "search", defaultValue = "") String search, @PageableDefault Pageable pageable) {
        if (pageable.getPageSize() > Constants.MAX_PAGE_SIZE) {
            pageable = new PageRequest(pageable.getPageNumber(), Constants.MAX_PAGE_SIZE);
        }

        return courseService.findCourseForCurrentSemesterWithName(search, pageable);
    }

    @ModelAttribute("searchString")
    private String getSearchString(@RequestParam(value = "search", defaultValue = "") String search) {
        return search;
    }

    @GetMapping
    public String courses() {
        return "/student/lehrveranstaltungen";
    }

    @GetMapping("/{id}")
    public String course(@PathVariable Long id, Model model, Principal principal) {
        log.debug("StudentEntity " + principal.getName() + " requesting course " + id + " details");

        StudentEntity student = studentService.findByUsername(principal.getName());

        model.addAttribute("course", courseService.courseDetailsFor(student, id));

        return "/student/course-details";
    }

}
