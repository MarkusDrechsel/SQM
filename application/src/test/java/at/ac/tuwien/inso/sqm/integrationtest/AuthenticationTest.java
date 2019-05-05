package at.ac.tuwien.inso.sqm.integrationtest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.inso.sqm.entity.Rolle;
import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.repository.UserAccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    public void itRedirectsToLoginErrorOnAuthenticationError() throws Exception {
        mockMvc.perform(
                formLogin().user("unknown").password("pass")
        ).andExpect(
                unauthenticated()
        ).andExpect(
                redirectedUrl("/login?error")
        );
    }

    @Test
    public void itAuthenticatesAdmin() throws Exception {
        userAccountRepository.save(new UserAccountEntity("admin", "pass", Rolle.ADMIN));

        mockMvc.perform(
                formLogin().user("admin").password("pass")
        ).andExpect(
                authenticated().withRoles(Rolle.ADMIN.name())
        ).andExpect(
                redirectedUrl("/")
        );
    }

    @Test
    public void itAuthenticatesLecturer() throws Exception {
        userAccountRepository.save(new UserAccountEntity("lecturer", "pass", Rolle.LECTURER));

        mockMvc.perform(
                formLogin().user("lecturer").password("pass")
        ).andExpect(
                authenticated().withRoles(Rolle.LECTURER.name())
        ).andExpect(
                redirectedUrl("/")
        );
    }

    @Test
    public void itAuthenticatesStudent() throws Exception {
        userAccountRepository.save(new UserAccountEntity("student", "pass", Rolle.STUDENT));

        mockMvc.perform(
                formLogin().user("student").password("pass")
        ).andExpect(
                authenticated().withRoles(Rolle.STUDENT.name())
        ).andExpect(
                redirectedUrl("/")
        );
    }

    @Test
    public void onLogoutUserIsNoLongerAuthenticatedAndRedirectedToLoginPage() throws Exception {
        mockMvc.perform(
                logout()
        ).andExpect(
                unauthenticated()
        ).andExpect(
                redirectedUrl("/login?loggedOut")
        );
    }

    @Test
    public void onUnauthorizedAccessItReturnsAccessDenied() throws Exception {
        mockMvc.perform(
                get("/admin").with(user("student"))
        ).andExpect(
                status().isForbidden()
        );
    }
}
