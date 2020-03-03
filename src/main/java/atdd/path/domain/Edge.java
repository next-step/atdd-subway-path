package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class Edge {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id")
    private Station source;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Station target;

    @OneToMany(fetch = LAZY)
    @JoinColumn(name = "id")
    private Line line;

    public Edge() {
    }

    @Builder
    public Edge(Long id, Station source, Station target, Line line) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.line = line;
    }
}
