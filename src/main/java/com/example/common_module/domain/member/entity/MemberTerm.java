package com.example.common_module.domain.member.entity;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
@Builder
public class MemberTerm {

    @Id
    @Column(name = "member_terms_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;

    @Column
    private Boolean accepted;

//    @Column
//    private LocalDate accepted_at;
}
