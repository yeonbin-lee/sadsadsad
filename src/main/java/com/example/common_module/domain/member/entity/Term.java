package com.example.common_module.domain.member.entity;

import com.example.common_module.domain.member.entity.enums.Mandatory;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Term {

    @Id
    @Column(name = "term_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    // 의무 여부
    @Enumerated(EnumType.STRING)
    private Mandatory mandatory;
//
//    @Column
//    private LocalDate created_at;
//
//    @Column
//    private LocalDate updated_at;


}
