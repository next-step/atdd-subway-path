package atdd.edge.domain;

import atdd.line.domain.Line;
import atdd.station.domain.Station;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
public class Edge {

	@Id
	@GeneratedValue
	@Column(name = "EDGE_ID")
	private Long id;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "LINE_ID")
	private Line line;

	@Column(name = "ELAPSED_TIME")
	private int elapsedTime;

	@Column(name = "DISTANCE")
	private int distance;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "SOURCE_STATION_ID")
	private Station sourceStation;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "TARGET_STATION_ID")
	private Station targetStation;
}
