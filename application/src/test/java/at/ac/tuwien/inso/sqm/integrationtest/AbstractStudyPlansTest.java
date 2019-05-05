package at.ac.tuwien.inso.sqm.integrationtest;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import at.ac.tuwien.inso.sqm.entity.EtcsDistributionEntity;
import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.StduyPlanEntity;
import at.ac.tuwien.inso.sqm.entity.SubjectForStudyPlanEntity;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.repository.StudyPlanRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectForStudyPlanRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractStudyPlansTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    protected StudyPlanRepository studyPlanRepository;

    @Autowired
    protected SubjectRepository subjectRepository;

    @Autowired
    protected SubjectForStudyPlanRepository subjectForStudyPlanRepository;

    protected List<Subjcet> subjects;

    protected StduyPlanEntity studyPlan1 = new StduyPlanEntity("Bachelor Software and Information Engineering", new EtcsDistributionEntity(new BigDecimal(90), new BigDecimal(60), new BigDecimal(30)));
    protected StduyPlanEntity studyPlan2 = new StduyPlanEntity("Master Business Informatics", new EtcsDistributionEntity(new BigDecimal(30), new BigDecimal(70), new BigDecimal(20)));
    protected StduyPlanEntity studyPlan3 = new StduyPlanEntity("Master Computational Intelligence", new EtcsDistributionEntity(new BigDecimal(60),new BigDecimal(30),new BigDecimal(30)));

    protected SubjectForStudyPlanEntity s1, s2, s3, s4, s5, s6;

    @Before
    public void setUp() {
        Iterable<Subjcet> subjectsIterable = subjectRepository.save(asList(
                new Subjcet("Algebra und Diskrete Mathematik für Informatik und Wirtschaftsinformatik", new BigDecimal(3.0)),
                new Subjcet("Software Engineering and Project Management", new BigDecimal(6.0)),
                new Subjcet("Advanced Software Engineering", new BigDecimal(6.0)),
                new Subjcet("Digital forensics", new BigDecimal(3.0)),
                new Subjcet("Model Engineering", new BigDecimal(6.0)),
                new Subjcet("Formale Methoden", new BigDecimal(6.0)),
                new Subjcet("Datenbanksysteme", new BigDecimal(6.0)),
                new Subjcet("Verteile Systeme", new BigDecimal(3.0))
        ));

        subjects = StreamSupport.stream(subjectsIterable.spliterator(), false).collect(Collectors.toList());
        studyPlanRepository.save(studyPlan1);
        s1 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(0), studyPlan1, true, 1));
        s2 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(1), studyPlan1, true, 1));
        s3 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(2), studyPlan1, true, 2));
        s4 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(3), studyPlan1, false, 3));
        s5 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(4), studyPlan1, false, 2));
        s6 = subjectForStudyPlanRepository.save(new SubjectForStudyPlanEntity(subjects.get(5), studyPlan1, true, 2));
    }

}
