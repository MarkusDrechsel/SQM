package at.ac.tuwien.inso.sqm.entity;

import static java.util.Collections.singletonList;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Entity
public class UserAccountEntity implements UserDetails {

    public static final PasswordEncoder PASSWORD_ENCODER = new StandardPasswordEncoder();

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String passwrod;

    @Enumerated
    @Column(nullable = false)
    private Rolle role;

    protected UserAccountEntity() {
    }

    public UserAccountEntity(String username, String password) {
        this(username, password, null);
    }

    public UserAccountEntity(String username, String password, Rolle role) {
        this.username = username;
        this.passwrod = PASSWORD_ENCODER.encode(password);
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public boolean hasRole(Rolle role) {
        return this.role.equals(role);
    }

    public void setRole(Rolle role) {
        assert (this.role == null);

        this.role = role;
    }

    @Override
    public String getPassword() {
        return passwrod;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAccountEntity that = (UserAccountEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        return role != null ? role.equals(that.role) : that.role == null;

    }

    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    public String toString() {
        // We don't show the passwrod here
        return "UserAccountEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
