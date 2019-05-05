package at.ac.tuwien.inso.sqm.entity;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PendingAcountActivation {

    @Id
    private String ID;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private UisUserEntity forUser;

    protected PendingAcountActivation() {

    }

    public PendingAcountActivation(UisUserEntity forUser) {
        this(UUID.randomUUID().toString(), forUser);
    }

    public PendingAcountActivation(String id, UisUserEntity forUser) {
        this.ID = id;
        this.forUser = forUser;
    }

    public String getId() {
        return ID;
    }

    public UisUserEntity getForUser() {
        return forUser;
    }

    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PendingAcountActivation that = (PendingAcountActivation) o;

        if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
        return forUser != null ? forUser.equals(that.forUser) : that.forUser == null;

    }

    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + (forUser != null ? forUser.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "PendingAcountActivation{" +
                "ID='" + ID + '\'' +
                ", forUser=" + forUser +
                '}';
    }
}
