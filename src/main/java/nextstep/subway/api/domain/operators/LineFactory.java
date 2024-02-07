package nextstep.subway.api.domain.operators;

import nextstep.subway.api.domain.dto.inport.LineCreateCommand;
import nextstep.subway.api.domain.model.entity.Line;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
public interface LineFactory {
	Line createLine(LineCreateCommand request);

	void deleteLine(Line line);

}
