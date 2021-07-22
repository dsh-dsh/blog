package main.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false, columnDefinition="tinytext")
    private String code;

    @Column(nullable = false, columnDefinition="tinytext")
    private String secretCode;
}
