package com.shippings.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "id_from")
    private long idFrom;

    @Column(name = "id_about")
    private long idAbout;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rate")
    private int rate;

    @Column(name = "date")
    private Date date;
}
