package main.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, columnDefinition="tinyint")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition="enum('NEW','ACCEPTED', 'DECLINED')")
    private ModerationStatus moderationStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moderator_id")
    private User moderatorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition="text")
    private String text;

    @Column(nullable = false)
    private int viewCount;

    @OneToMany(mappedBy = "tag")
    private Set<TagPost> tags;


}
