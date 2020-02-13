package atdd.line.dto;

import atdd.edge.domain.Edge;
import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateEdgeRequest {

	private double elapsedTime;
	private double distance;

	@Builder
	public CreateEdgeRequest(final double elapsedTime, final double distance) {
		this.elapsedTime = elapsedTime;
		this.distance = distance;
	}

	public Edge toEntity(final Station sourceStation, final Station targetStation) {
		return Edge.builder()
			.elapsedTime(elapsedTime)
			.distance(distance)
			.sourceStation(sourceStation)
			.targetStation(targetStation)
			.build();
	}
}
