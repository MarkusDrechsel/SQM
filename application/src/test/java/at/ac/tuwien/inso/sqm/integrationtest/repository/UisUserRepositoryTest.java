package at.ac.tuwien.inso.sqm.integrationtest.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.entity.UisUserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.StudentEntity;
import at.ac.tuwien.inso.sqm.repository.UisUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UisUserRepositoryTest {

    @Autowired
    private UisUserRepository uisUserRepository;

    private Map<String, UisUserEntity> uisUsers = new HashMap<String, UisUserEntity>() {
        {
            put("lecturer1", new LecturerEntity("l12345", "lecturer1", "l12345@uis.at"));
            put("lecturer2", new LecturerEntity("l54321", "lecturer2", "l54321@uis.at"));
            put("student", new StudentEntity("s12345", "student", "s12345@uis.at"));
        }
    };

    @Before
    public void setUp() {
        uisUserRepository.save(uisUsers.values());
    }

    @Test
    public void testFindAllMatchingSomeExistingUsersUsingIdentificationNumber() {
        Page<UisUserEntity> foundUsers = uisUserRepository.findAllMatching("12345", null);
        assertTrue(foundUsers.getContent().contains(uisUsers.get("lecturer1")));
        assertTrue(foundUsers.getContent().contains(uisUsers.get("student")));
    }

    @Test
    public void testFindAllMatchingSomeExistingUsersUsingNameIgnoreCase() {
        Page<UisUserEntity> foundUsers = uisUserRepository.findAllMatching("lecTURer", null);
        assertTrue(foundUsers.getContent().contains(uisUsers.get("lecturer1")));
        assertTrue(foundUsers.getContent().contains(uisUsers.get("lecturer2")));
    }

    @Test
    public void testFindAllMatchingSomeExistingUsersUsingEmail() {
        Page<UisUserEntity> foundUsers = uisUserRepository.findAllMatching("s12345@uis.at", null);
        assertTrue(foundUsers.getContent().contains(uisUsers.get("student")));
    }

    @Test
    public void testFindAllMatchingNoContent() {
        Page<UisUserEntity> foundUsers = uisUserRepository.findAllMatching("some_non_existing_content", null);
        assertFalse(foundUsers.hasContent());
    }

    @Test
    public void testExistsForExistingIdentificationNumber() throws Exception {
        LecturerEntity lecturer1 = (LecturerEntity) uisUsers.get("lecturer1");
        assertTrue(uisUserRepository.existsByIdentificationNumber(lecturer1.getIdentificationNumber()));
    }

    @Test
    public void testExistsForNonExistingIdentificationNumber() throws Exception {
        assertFalse(uisUserRepository.existsByIdentificationNumber("some_non_existing_id"));
    }
}

