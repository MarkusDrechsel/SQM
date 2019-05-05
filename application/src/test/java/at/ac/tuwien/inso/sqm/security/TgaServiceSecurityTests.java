package at.ac.tuwien.inso.sqm.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.service.TgaService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TgaServiceSecurityTests {

    @Autowired
    private TgaService tgaService;

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void searchForSubjectsNotAuthenticated() {
        tgaService.findAll();
    }

    @Test
    @WithMockUser
    public void searchForSubjectsAuthenticated() {
        tgaService.findAll();
    }
}
