package com.tmartrano.succubot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "movies")
public class MovieEntry implements Serializable {

    private static final long serialVersionUID = -8402611044513083864L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "username")
    String username;

    @Column(name = "title")
    String movieTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    MovieCategory category;
}
