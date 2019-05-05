package at.ac.tuwien.inso.sqm.integrationtest;

import java.math.BigDecimal;

import at.ac.tuwien.inso.sqm.entity.Rolle;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import at.ac.tuwien.inso.sqm.entity.UserAccountEntity;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractSubjectsTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected SubjectRepository subjectRepository;

    @Autowired
    protected LecturerRepository lecturerRepository;

    protected UserAccountEntity user1 = new UserAccountEntity("lecturer1", "pass", Rolle.LECTURER);
    protected LecturerEntity lecturer1 = new LecturerEntity("l0001", "LecturerEntity 1", "email1@uis.at", user1);
    protected LecturerEntity lecturer2 = new LecturerEntity("l0002", "LecturerEntity 2", "email2@uis.at", new UserAccountEntity("lecturer2", "pass", Rolle.LECTURER));
    protected LecturerEntity lecturer3 = new LecturerEntity("l0003", "LecturerEntity 3", "email3@uis.at", new UserAccountEntity("lecturer3", "pass", Rolle.LECTURER));
    protected Subjcet calculus = new Subjcet("Calculus", new BigDecimal(3.0));
    protected Subjcet sepm = new Subjcet("SEPM", new BigDecimal(6.0));
    protected Subjcet ase = new Subjcet("ASE", new BigDecimal(6.0));

    @Before
    public void setUp(){
        lecturerRepository.save(lecturer1);
        lecturerRepository.save(lecturer2);
        lecturerRepository.save(lecturer3);
    }




}
