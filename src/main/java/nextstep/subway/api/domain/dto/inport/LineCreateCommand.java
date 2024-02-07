package nextstep.subway.api.domain.dto.inport;

import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.interfaces.dto.request.LineCreateRequest;
import nextstep.subway.common.mapper.ModelMapperBasedObjectMapper;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineCreateCommand {

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

	public static LineCreateCommand from(LineCreateRequest createRequest) {
		return ModelMapperBasedObjectMapper.convert(createRequest, LineCreateCommand.class);
	}
}
