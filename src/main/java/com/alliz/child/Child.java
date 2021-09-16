package com.alliz.child;

import com.alliz.account.Account;
import com.alliz.enrollment.Enrollment;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "account")
public class Child {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate birth;

    private String schoolName;

    private String groupName;

    private String phone;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "child")
    private List<Enrollment> enrollments = new ArrayList<>();

    public void setAccount(Account account) {
        this.account = account;
        account.getChildren().add(this);
    }
}
