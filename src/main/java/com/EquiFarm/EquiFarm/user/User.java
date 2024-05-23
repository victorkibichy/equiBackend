package com.EquiFarm.EquiFarm.user;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.token.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User extends TrackingEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;
   

    @Column(name = "firstname", length = 50, nullable = false)
    private String firstName;

    @Column(name = "lastname", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150, nullable = true)
    private String email;

    @Column(name = "phoneNo", length = 20, nullable = false, unique = true)
    private String phoneNo;

//    @Column(name = "email", length = 150, unique = true)
//    private String email;
//
//    @Column(name = "phoneNo", length = 20, unique = true)
//    private String phoneNo;

    @Column(name = "password", length = 255, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "active", columnDefinition = "boolean default false")
    @Builder.Default
    private boolean isActive=true;

    @Column(name = "staff", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isStaff=false;

    @Column(name = "admin", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isAdmin=false;

    @Column(name = "national_id", length = 20, nullable = false, unique = true)
    private String nationalId;

    @Column(name = "latitude", nullable = true)
    private Double latitude = -0.0236;

    @Column(name = "longitude", nullable = true)
    private Double longitude = 37.9062;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
        // return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

//    @Override
//    @JsonIgnore
//    public String getUsername() {
//        return email;
//    }
    @Override
    @JsonIgnore
    public String getUsername() {
        return nationalId;
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

}
