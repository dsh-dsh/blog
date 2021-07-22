package main.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class TagPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
