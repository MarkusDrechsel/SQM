package at.ac.tuwien.inso.sqm.integrationtest.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.entity.Rolle;
import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.UisUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LecturerRepositoryTest {

    @Autowired
    private UisUserRepository uisUserRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Test
    public void findLecturerByAccountIdSuccessTest() {

        // create lecturer account
        UserAccountEntity account = new UserAccountEntity("lecturer", "pass", Rolle.LECTURER);
        LecturerEntity expectedLecturer = uisUserRepository.save(new LecturerEntity("l0100011", "Una Walker", "una.walker@uis.at", account));
        Long accountId = account.getId();

        // assert the create lecturer matches with the one found
        LecturerEntity actualLecturer = lecturerRepository.findLecturerByAccountId(accountId);
        assertEquals(expectedLecturer, actualLecturer);
    }

    @Test
    public void findLecturerByAccountIdFailureTest() {

        // search by some invalid id and make sure null is returned
        Long accountId = new Long(-1);
        LecturerEntity actualLecturer = lecturerRepository.findLecturerByAccountId(accountId);
        assertNull(actualLecturer);
    }
}
