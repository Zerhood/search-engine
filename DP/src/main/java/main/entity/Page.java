package main.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "page")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "path", nullable = false)
    private String path;
    @Column(name = "code", nullable = false)
    private int code;
    @Column(name = "content", nullable = false, length = 65555)
    private String content;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "page")
    private List<Index> index;
}