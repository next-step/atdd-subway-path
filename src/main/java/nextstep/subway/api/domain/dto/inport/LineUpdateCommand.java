package nextstep.subway.api.domain.dto.inport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.interfaces.dto.request.LineUpdateRequest;
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
public class LineUpdateCommand {
	private String name;
	private String color;

	public static LineUpdateCommand from(LineUpdateRequest updateRequest) {
		return ModelMapperBasedObjectMapper.convert(updateRequest, LineUpdateCommand.class);
	}
}
