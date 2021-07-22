package main.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private PostComment parent;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false, columnDefinition="text")
    private String text;

}
