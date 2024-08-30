package com.example.common_module.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

//@NoArgsConstructor
@Entity
@Getter
public class Term {

    @Id
    @Column(name = "term_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    private boolean mandatory; // 필수 여부

    private LocalDate createdDate;

//    // 의무 여부
//    @Enumerated(EnumType.STRING)
//    private Mandatory mandatory;
//
//    @Column
//    private LocalDate created_at;
//
//    @Column
//    private LocalDate updated_at;


}
