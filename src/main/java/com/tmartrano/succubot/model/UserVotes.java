package com.tmartrano.succubot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "uservotes")
public class UserVotes implements Serializable {

    @Tolerate
    public UserVotes() {
    }

    private static final long serialVersionUID = -8402611044513083864L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = "entry_id")
    UUID entryId;

    @Column(name = "username")
    String username;
}
