package atdd.path.domain;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class StationLine {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "station_line_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
}
