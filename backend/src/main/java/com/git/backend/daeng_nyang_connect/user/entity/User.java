package com.git.backend.daeng_nyang_connect.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.animal.entity.AnimalScrap;
import com.git.backend.daeng_nyang_connect.stomp.ChatRoom;
import com.git.backend.daeng_nyang_connect.stomp.ChatRoomUser;
import com.git.backend.daeng_nyang_connect.user.role.CustomGrantedAuthority;
import com.git.backend.daeng_nyang_connect.user.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userId;

    private String name;
    private String email;
    private String password;

    @Column(name = "user_nickname")
    private String nickname;
    private String city;
    private String town;
    private char gender;
    private String mobile;
    private Boolean experience;
    private String rawPassword;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MyPage myPage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "scrap")
    private List<AnimalScrap> myAnimalScrap;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatRoomUser> chatRooms = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new CustomGrantedAuthority(this.role));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
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