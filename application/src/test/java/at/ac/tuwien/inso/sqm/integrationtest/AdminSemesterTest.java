package at.ac.tuwien.inso.sqm.integrationtest;

import static java.util.Arrays.asList;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.integrationtest.clock.FixedClock;
import at.ac.tuwien.inso.sqm.integrationtest.clock.FixedClockListener;
import at.ac.tuwien.inso.sqm.dto.SemesterDto;
import at.ac.tuwien.inso.sqm.entity.Semester;
import at.ac.tuwien.inso.sqm.repository.SemestreRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FixedClock
@TestExecutionListeners({FixedClockListener.class, DependencyInjectionTestExecutionListener.class})
public class AdminSemesterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private SemestreRepository semesterRepository;

    @After
    public void terDown() {
        semesterRepository.deleteAll();
    }

    @Test
    @FixedClock("2016-11-11T11:00:00Z")
    public void itListsAllSemesters() throws Exception {
        // no new semester should be added since integrationtest already exists
        Semester ss2016 = semesterRepository.save(new Semester(2016, SemestreTypeEnum.SummerSemester));
        Semester ws2016 = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));

        mockMvc.perform(
                get("/admin/semester").with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("allSemesters", asList(ws2016.toDto(), ss2016.toDto()))
        ).andExpect(
                model().attribute("currentSemester", ws2016.toDto())
        );
    }

    @Test
    @FixedClock("2020-11-11T11:00:00Z")
    public void itListsCurrentSemesterAsWS2020() throws Exception {
        SemesterDto expected = new SemesterDto(2020, SemestreTypeEnum.WinterSemester);

        mockMvc.perform(
                get("/admin/semester").with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("allSemesters", asList(expected))
        ).andExpect(
                model().attribute("currentSemester", expected)
        );
    }

    @Test
    @FixedClock("2019-05-11T11:00:00Z")
    public void itListsCurrentSemesterAsWS2019WithOlderSemesters() throws Exception {

        // 2 older semesters already given
        Semester ss2016 = semesterRepository.save(new Semester(2016, SemestreTypeEnum.SummerSemester));
        Semester ws2016 = semesterRepository.save(new Semester(2016, SemestreTypeEnum.WinterSemester));

        SemesterDto expected = new SemesterDto(2019, SemestreTypeEnum.SummerSemester);

        mockMvc.perform(
                get("/admin/semester").with(user("admin").roles("ADMIN"))
        ).andExpect(
                model().attribute("allSemesters", asList(expected, ws2016.toDto(), ss2016.toDto()))
        ).andExpect(
                model().attribute("currentSemester", expected)
        );
    }

}
