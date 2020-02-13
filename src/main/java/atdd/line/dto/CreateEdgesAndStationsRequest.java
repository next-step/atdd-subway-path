package atdd.line.dto;

import atdd.station.dto.CreateStationRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateEdgesAndStationsRequest {

	private CreateEdgeRequest edge;
	private CreateStationRequest sourceStation;
	private CreateStationRequest targetStation;

	@Builder
	public CreateEdgesAndStationsRequest(final CreateEdgeRequest edge,
										 final CreateStationRequest sourceStation,
										 final CreateStationRequest targetStation) {
		this.edge = edge;
		this.sourceStation = sourceStation;
		this.targetStation = targetStation;
	}
}
