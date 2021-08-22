package com.alliz.domain;

import lombok.*;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "children")
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private LocalDateTime joinedAt;

    @OneToMany(mappedBy = "account")
    private List<Child> children = new ArrayList<>();

    private String location;

    private String phone;

    private String kakaoTalkId;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @Lob @Basic(fetch = FetchType.LAZY)
    private String banner;

    private boolean childTakingByWeb;

    private boolean childTakingByEmail;

    private boolean childBringBackByWeb;

    private boolean childBringBackByEmail;

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
}
