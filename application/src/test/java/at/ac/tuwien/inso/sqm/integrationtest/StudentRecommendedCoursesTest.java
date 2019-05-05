package at.ac.tuwien.inso.sqm.integrationtest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StudentRecommendedCoursesTest extends AbstractCoursesTest {


    @Test
    public void studentRecommendedCoursesTest() throws Exception {
        mockMvc.perform(
                get("/student/recommended").with(user(studentUserAccount))
        ).andExpect(
                model().attributeExists("recommendedCourses")
        );
    }

    @Test
    @Rollback
    public void dismissCourseTest() throws Exception {
        mockMvc.perform(
                post("/student/recommended")
                        .with(user(studentUserAccount))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .content("dismissedId=" + calculusSS2019.getId())
        ).andExpect(
                result -> {
                    if (stduentRepository.findByAccount(studentUserAccount).getDismissedCourses().size() <= 0) {
                        throw new Exception("Size of dismissed courses for student " + student + " is zero");
                    }
                    if (!stduentRepository.findByAccount(studentUserAccount).getDismissedCourses().contains(calculusSS2019)) {
                        throw new Exception("Wrong dismissed course!");
                    }
                }
        ).andExpect(
                redirectedUrl("/student/recommended")
        );
    }
}
