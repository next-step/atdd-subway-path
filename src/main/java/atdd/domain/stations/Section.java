package atdd.domain.stations;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne
    @JoinColumn(name = "station_id")
    private Stations source;

    @OneToOne
    @JoinColumn(name = "station_id")
    private Stations target;

    public Section next = null;
    public Section prev = null;
    private Double distance = 0.0;

    @Builder
    public Section(Line line, Stations source, Stations target) {
        this.line = line;
        this.source = source;
        this.target = target;
    }

    public Section(Stations source, Stations target) {
        this.source = source;
        this.target = target;
    }

    public void deleteSource(Stations toBeDeleted) {
        this.source = null;
    }

    public void deleteTarget(Stations toBeDeleted) {
        this.target = null;
    }

    public void changeSource(Stations newSource) {
        this.source = newSource;
    }

    public void changeTarget(Stations newTarget) {
        this.source = newTarget;
    }
}
