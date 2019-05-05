package at.ac.tuwien.inso.sqm.service;

import at.ac.tuwien.inso.sqm.entity.LecturerEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class LecturerServiceTest {

    private LecturerService lecturerService;

    @Mock
    private LecturerEntity lecturer = new LecturerEntity("l12345", "lecturer", "l12345@uis.at");

    @Before
    public void setUp() {
        this.lecturerService = new LecturerServiceImpl();
        when(lecturer.getTwoFactorSecret()).thenReturn("12345");
    }

    @Test
    public void generateQRUrlTest() throws UnsupportedEncodingException {

        String expectedUrl = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth%3A%2F%2Ftotp%2FUIS%3Anull%3Fsecret%3D12345%26issuer%3DUIS";
        String actualUrl = lecturerService.generateQRUrl(lecturer);

        assertEquals(expectedUrl, actualUrl);
    }
}
