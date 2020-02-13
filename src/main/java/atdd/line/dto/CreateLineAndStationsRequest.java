package atdd.line.dto;

import atdd.line.domain.Line;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateLineAndStationsRequest {

	private Line line;
	private List<CreateEdgesAndStationsRequest> edgesAndStations;

	@Builder
	public CreateLineAndStationsRequest(final Line line, final List<CreateEdgesAndStationsRequest> edgesAndStations) {
		this.line = line;
		this.edgesAndStations = edgesAndStations;
	}
}
