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
@Table(name = "poll")
public class PollEntry implements Serializable {

    private static final long serialVersionUID = -8402611044513083864L;

    @Tolerate
    public PollEntry() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = "entry_id")
    private UUID entryId;

    @Column(name = "description")
    private String pollEntryDescription;

    @Column(name = "entry_number")
    private int pollEntryNumber;

    @Column(name = "vote_tally")
    private int voteTally;

    @Column(name = "is_active")
    private boolean isActive;
}
