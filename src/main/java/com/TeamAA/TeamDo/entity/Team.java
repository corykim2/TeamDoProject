package com.TeamAA.TeamDo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<User> members = new ArrayList<>();

    public Team() {}

    public Team(String name) { this.name = name; }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
}
