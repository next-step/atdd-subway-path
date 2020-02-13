package atdd.line.dto;

import atdd.line.domain.Line;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateLineRequest {

	private String name;
	private String startTime;
	private String endTime;
	private String intervalTime;

	@Builder
	public CreateLineRequest(final String name, final String startTime, final String endTime, final String intervalTime) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
	}

	public Line toEntity() {
		return Line.builder()
			.name(name)
			.startTime(startTime)
			.endTime(endTime)
			.intervalTime(intervalTime)
			.build();
	}
}
