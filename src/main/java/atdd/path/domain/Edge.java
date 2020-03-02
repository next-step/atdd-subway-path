package atdd.path.domain;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Edge {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "edge_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "station_id", insertable = false, updatable = false)
    private Station sourceStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "station_id", insertable = false, updatable = false)
    private Station targetStation;

}
