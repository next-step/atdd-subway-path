package nextstep.subway.applicaion.dto;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import lombok.Getter;
import nextstep.subway.domain.Station;

@Getter
public class PathResponse {
	private List<StationResponse> stations;
	private int distance;

	private PathResponse(List<StationResponse> stations, int distance) {
		this.stations = stations;
		this.distance = distance;
	}

	public static PathResponse from(GraphPath<Station, DefaultWeightedEdge> graphPath) {
		List<StationResponse> stations = new ArrayList<>();
		graphPath.getVertexList().forEach(station -> {
			stations.add(new StationResponse(station.getId(), station.getName()));
		});
		return new PathResponse(stations, (int)graphPath.getWeight());
	}
}
