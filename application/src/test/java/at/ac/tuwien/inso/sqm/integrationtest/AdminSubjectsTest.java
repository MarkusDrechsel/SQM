package at.ac.tuwien.inso.sqm.integrationtest;

import at.ac.tuwien.inso.sqm.entity.*;
import at.ac.tuwien.inso.sqm.repository.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;
import org.springframework.transaction.annotation.*;

import java.math.*;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminSubjectsTest extends AbstractSubjectsTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemestreRepository semesterRepository;

    @Test
    public void adminShouldSeeSubjectsOfFirstPage() throws Exception {

        // when subjects are created
        subjectRepository.save(calculus);
        subjectRepository.save(sepm);
        subjectRepository.save(ase);

        // then the admin should see them all in the first page
        mockMvc.perform(
                get("/admin/subjects").with(user("admin").roles("ADMIN")))
                .andExpect(model().attribute("subjects", asList(calculus, sepm, ase))
        );
    }
    
    @Test
    public void adminShouldBeAbleToDeleteSubjects() throws Exception {

        Subjcet cal = subjectRepository.save(calculus);
        subjectRepository.save(sepm);
        subjectRepository.save(ase);

        // then the admin should see them all in the first page
        mockMvc.perform(
                post("/admin/subjects/remove/"+cal.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
        ).andExpect(
                redirectedUrl("/admin/subjects")
        );
        
        assertFalse(subjectRepository.exists(cal.getId()));
    }
    
    @Test
    public void adminSubjectShouldNotBeRemovedIfItContainsCourses() throws Exception {

        Subjcet cal = subjectRepository.save(calculus);
        subjectRepository.save(sepm);
        subjectRepository.save(ase);
        
        Semester ws2016 = new Semester(2016, SemestreTypeEnum.WinterSemester);
        semesterRepository.save(ws2016);
        Lehrveranstaltung calculusWS2016o = new Lehrveranstaltung(cal, ws2016);
        
        Lehrveranstaltung calculusWS2016 = courseRepository.save(calculusWS2016o);
        
        

        // then the admin should see them all in the first page
        mockMvc.perform(
                post("/admin/subjects/remove/"+cal.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
        ).andExpect(
            redirectedUrl("/admin/subjects")
        );
        
        assertTrue(subjectRepository.exists(cal.getId()));
    }


    @Test
    public void adminShouldSeeSubjectDetails() throws Exception {

        // when subjects are created and assigned to lecturers
        sepm.addLecturers(lecturer1);
        ase.addLecturers(lecturer1, lecturer2);
        Long id1 = subjectRepository.save(calculus).getId();
        Long id2 = subjectRepository.save(sepm).getId();
        Long id3 = subjectRepository.save(ase).getId();

        // admin should see subject calculus without any lecturers
        mockMvc.perform(
                get("/admin/subjects/"+id1).with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("subject", calculus)
        ).andExpect(
                model().attribute("lecturers", asList())
        );

        //admin should see subject sepm with lecturer1
        mockMvc.perform(
                get("/admin/subjects/"+id2).with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("subject", sepm)
        ).andExpect(
                model().attribute("lecturers", asList(lecturer1))
        );

        //admin should see subject ase with lecturer1 and lecturer2
        mockMvc.perform(
                get("/admin/subjects/"+id3).with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("subject", ase)
        ).andExpect(
                model().attribute("lecturers", asList(lecturer1, lecturer2))
        );
    }

    @Test
    public void adminCreateSubjectTest() throws Exception {

        String name = "maths";
        BigDecimal ects = new BigDecimal(6.0);

        mockMvc.perform(
                post("/admin/subjects/create")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("name", name)
                        .param("ects", ects.toString())
                        .with(csrf())
        );

        Subjcet subject = subjectRepository.findByNameContainingIgnoreCase(name).get(0);
        assertEquals(name, subject.getName());
        assertEquals(ects, subject.getEcts());

    }

    @Test
    public void adminListSubjectsForPageSearchNullAndPageNumberOneTest() throws Exception {

        mockMvc.perform(
                get("/admin/subjects/page/1")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("pageNumber", "1")
                        .with(csrf())
        ).andExpect(
                redirectedUrl("/admin/subjects")
        );

    }

    @Test
    public void adminListSubjectsForPageSearchEmptyTest() throws Exception {

        mockMvc.perform(
                get("/admin/subjects/page/1")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("search", "")
                        .param("pageNumber", "1")
                        .with(csrf())
        ).andExpect(
                redirectedUrl("/admin/subjects/page/1")
        );

    }

    @Test
    public void adminListSubjectsForPagePageNumberOneTest() throws Exception {

        mockMvc.perform(
                get("/admin/subjects/page/1")
                        .with(user("admin").roles(Rolle.ADMIN.name()))
                        .param("search", "something")
                        .param("pageNumber", "1")
                        .with(csrf())
        ).andExpect(
                redirectedUrl("/admin/subjects?search=something")
        );

    }



}
