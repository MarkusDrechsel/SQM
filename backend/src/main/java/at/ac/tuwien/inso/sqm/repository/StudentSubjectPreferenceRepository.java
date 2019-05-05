package at.ac.tuwien.inso.sqm.repository;

import at.ac.tuwien.inso.sqm.service.student_subject_prefs.StudentSubjectPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentSubjectPreferenceRepository extends MongoRepository<StudentSubjectPreference, String> {

    void deleteByStudentIdAndSubjectId(Long studentId, Long subjectId);

    StudentSubjectPreference findByStudentIdAndSubjectId(Long studentId, Long subjectId);
}
