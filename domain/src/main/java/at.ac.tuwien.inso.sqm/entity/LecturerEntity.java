package at.ac.tuwien.inso.sqm.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.jboss.aerogear.security.otp.api.Base32;

@Entity
public class LecturerEntity extends UisUserEntity {

    @ManyToMany(mappedBy = "lecturers")
    private List<Subjcet> subjects = new ArrayList<>();

    private String twoFactorSecret;

    protected LecturerEntity() {

    }

    public LecturerEntity(String identificationNumber, String name, String email) {
        this(identificationNumber, name, email, null);
    }

    public LecturerEntity(String identificationNumber, String name, String email, UserAccountEntity account) {
        super(identificationNumber, name, email, account);
        this.twoFactorSecret = Base32.random();
    }

    @Override
    protected void adjustRole(UserAccountEntity account) {
        account.setRole(Rolle.LECTURER);
    }

    public List<Subjcet> getSubjects() {
        return subjects;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }
}
