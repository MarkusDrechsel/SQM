package at.ac.tuwien.inso.sqm.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import at.ac.tuwien.inso.sqm.entity.Subjcet;
import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import at.ac.tuwien.inso.sqm.repository.LecturerRepository;
import at.ac.tuwien.inso.sqm.repository.SubjectRepository;

@Service
public class LecturerServiceImpl implements LecturerService {
	
	private static final Logger log = LoggerFactory.getLogger(LecturerServiceImpl.class);


    public static String QR_PREFIX =
            "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String app_name =
            "UIS";

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    @Transactional(readOnly = true)
    public LecturerEntity getLoggedInLecturer() {
        Long id = userAccountService.getCurrentLoggedInUser().getId();
        log.info("returning currently logged in lecturer with id "+id);
        return lecturerRepository.findLecturerByAccountId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subjcet> getOwnSubjects() {
    	log.info("returning owned subjects for currently logged in lecturer");
        return subjectRepository.findByLecturers_Id(getLoggedInLecturer().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subjcet> findSubjectsFor(LecturerEntity lecturer) {
    	log.info("returning subjects for lecturer "+lecturer.toString());
        return subjectRepository.findByLecturers_Id(lecturer.getId());
    }

    @Override
    public String generateQRUrl(LecturerEntity lecturer) throws UnsupportedEncodingException {
    	log.info("generating QR url for lecturer with id "+lecturer.getId());
        String url = "otpauth://totp/" + app_name
                + ":" + lecturer.getEmail()
                + "?secret=" + lecturer.getTwoFactorSecret()
                + "&issuer=" + app_name + "";
        return QR_PREFIX + URLEncoder.encode(url, "UTF-8");
    }
}
