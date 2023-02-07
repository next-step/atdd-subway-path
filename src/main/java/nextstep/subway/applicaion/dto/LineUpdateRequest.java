package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LineUpdateRequest {

	private String name;

	private String color;

	public LineUpdateRequest(String name, String color) {
		this.name = name;
		this.color = color;
	}
}
