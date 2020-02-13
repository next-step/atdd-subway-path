package atdd.edge.domain;

import atdd.line.domain.Line;
import atdd.station.domain.Station;
import lombok.Builder;
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
	private double elapsedTime;	// 소요시간(분)

	@Column(name = "DISTANCE")
	private double distance;		// 거리(km)

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "SOURCE_STATION_ID")
	private Station sourceStation;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "TARGET_STATION_ID")
	private Station targetStation;

	@Builder
	public Edge(final Line line, double elapsedTime, double distance, Station sourceStation, Station targetStation) {
		this.line = line;
		this.elapsedTime = elapsedTime;
		this.distance = distance;
		this.sourceStation = sourceStation;
		this.targetStation = targetStation;
	}

	public void applyStations(Station sourceStation, Station targetStation) {
		this.sourceStation = sourceStation;
		this.targetStation = targetStation;
	}

	public void applyLine(final Line line) {
		this.line = line;
	}
}
