package main.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @JsonIgnoreProperties("tag")
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<TagPost> posts = new HashSet<>();

    @Override
    public String toString() {
        return name;
    }

}
