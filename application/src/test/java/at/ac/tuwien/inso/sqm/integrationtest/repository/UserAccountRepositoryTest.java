package at.ac.tuwien.inso.sqm.integrationtest.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import at.ac.tuwien.inso.sqm.entity.Rolle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import at.ac.tuwien.inso.sqm.repository.UserAccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private UserAccountEntity existingUserAccount;

    @Before
    public void setUp() {
        this.existingUserAccount = new UserAccountEntity("user1", "pass");
        this.existingUserAccount.setRole(Rolle.ADMIN);
        userAccountRepository.save(existingUserAccount);
    }

    @Test
    public void testExistsForExistingUsername() throws Exception {
        assertTrue(userAccountRepository.existsByUsername(existingUserAccount.getUsername()));
    }

    @Test
    public void testExistsForNonExistingUsername() throws Exception {
        assertFalse(userAccountRepository.existsByUsername("some_non_existing_username"));
    }
}
