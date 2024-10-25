package com.springboot.tarot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name ="Tarots")
@NoArgsConstructor
public class Tarot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tarotId;

    @Column(name = "tarotName", nullable = false, length = 50)
    private String name;

    @Column(name = "tarotMeaning", nullable = false, length = 50)
    private String meaning;

}
