package at.ac.tuwien.inso.sqm.security;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import java.util.List;

import at.ac.tuwien.inso.sqm.entity.SemestreTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.dto.SemesterDto;
import at.ac.tuwien.inso.sqm.service.SemesterServiceInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SemesterServiceSecurityTests {

    @Autowired
    private SemesterServiceInterface semesterService;

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void createNotAuthenticated() {
        SemesterDto ws2016 = new SemesterDto(2016, SemestreTypeEnum.WinterSemester);
        semesterService.create(ws2016);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "STUDENT")
    public void createAuthenticatedAsStudent() {
        SemesterDto ws2016 = new SemesterDto(2016, SemestreTypeEnum.WinterSemester);
        semesterService.create(ws2016);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "LECTURER")
    public void createAuthenticatedAsLecturer() {
        SemesterDto ws2016 = new SemesterDto(2016, SemestreTypeEnum.WinterSemester);
        semesterService.create(ws2016);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createAuthenticatedAsAdmin() {
        SemesterDto ws2016 = new SemesterDto(2016, SemestreTypeEnum.WinterSemester);

    	SemesterDto result = semesterService.create(ws2016);
        assertEquals(result.getLabel(), "2016 WS");
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void getCurrentSemesterNotAuthenticated() {
        semesterService.getCurrentSemester();
    }

    @Test
    @WithMockUser
    public void getCurrentSemesterAuthenticated() {
    	SemesterDto result = semesterService.getCurrentSemester();
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void findAllNotAuthenticated() {
        semesterService.findAll();
    }

    @Test
    @WithMockUser
    public void findAllAuthenticated() {
        List<SemesterDto> result = semesterService.findAll();
        assertNotNull(result);
    }
}
