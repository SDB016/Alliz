package com.alliz.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@ToString(exclude = "account")
public class Child {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private int age;

    private String school;

    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public void setAccount(Account account) {
        this.account = account;
        account.getChildren().add(this);
    }
}
