package com.alliz.account;

import com.alliz.child.Child;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "children")
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Child> children = new HashSet<>();

    private String location;

    private String phone;

    private String kakaoTalkId;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String banner;

    private boolean useBanner;

    private boolean childTakingByWeb;

    private boolean childTakingByEmail;

    private boolean childBringBackByWeb;

    private boolean childBringBackByEmail;

    @Builder
    public Account(String nickname, String email, String profileImage, Role role) {
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
        this.children = new HashSet<>();
        this.setChildTakingByWeb(true);
        this.setChildBringBackByWeb(true);
    }

    public Account update(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public String getBanner() {
        return banner != null ? banner : "/images/default_banner.png";
    }

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp() {
        this.setEmailVerified(true);
        this.setJoinedAt(LocalDateTime.now());
    }

    public boolean isValidToken(String token) {
        return this.getEmailCheckToken().equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public boolean isAdmin() {
        return this.getRole().equals(Role.ADMIN);
    }
}
