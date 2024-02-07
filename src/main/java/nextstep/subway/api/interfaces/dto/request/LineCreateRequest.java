package nextstep.subway.api.interfaces.dto.request;

import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineCreateRequest {

	@Description(value = "신분당선")
	private String name;
	@Description(value = "bg-red-600")
	private String color;
	@Description(value = "1")
	private Long upStationId;
	@Description(value = "2")
	private Long downStationId;
	@Description(value = "10")
	private Long distance;
}
