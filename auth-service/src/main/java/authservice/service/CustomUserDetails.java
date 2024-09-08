package authservice.service;

import authservice.entities.UserInfo;
import authservice.entities.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class is used to provide user details for authentication and authorization.
 */
public class CustomUserDetails extends UserInfo implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructor to initialize CustomUserDetails with UserInfo object.
     * This constructor sets the username, password, and authorities from the UserInfo object.
     *
     * @param byUsername the UserInfo object containing user details
     */
    public CustomUserDetails(UserInfo byUsername) {
        this.username = byUsername.getUsername();
        this.password = byUsername.getPassword();
        List<GrantedAuthority> auths = new ArrayList<>();

        // Convert UserRole objects to GrantedAuthority and add to the list
        for (UserRole role : byUsername.getRoles()) {
            // Convert role name to uppercase and wrap it in SimpleGrantedAuthority
            auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
        }
        this.authorities = auths;
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is non-expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Return true if account is not expired
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user's account is non-locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Return true if account is not locked
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if the credentials are non-expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Return true if credentials are not expired
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return true; // Return true if user is enabled
    }
}
