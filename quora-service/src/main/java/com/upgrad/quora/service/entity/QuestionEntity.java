package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "question")
@NamedQueries(
        {
                @NamedQuery(name = "questionsByUser", query = "select q from QuestionEntity q where q.user = :id"),
                @NamedQuery(name = "questionById", query = "select q from QuestionEntity q where q.uuid =:id"),
                @NamedQuery(name = "findAll", query = "select q from QuestionEntity q")
        }
)
public class QuestionEntity {
    public static final String FIND_ALL = "findAll";
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @Column(name = "CONTENT")
    @NotNull
    @Size(max = 500)
    private String content;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
