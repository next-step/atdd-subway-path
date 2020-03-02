package atdd.path.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
public class Station {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;

    @Transient
    private List<Line> lines = new ArrayList<>();

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    @Builder
    public Station(Long id, String name, List<Line> lines) {
        this.id = id;
        this.name = name;
        this.lines = lines;
    }

    public void addLine(Line line){
        this.lines.add(line);
    }
}

