package main.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

}
