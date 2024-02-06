package nextstep.subway.api.infrastructure.operators;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nextstep.subway.api.domain.dto.inport.LineCreateCommand;
import nextstep.subway.api.domain.model.entity.Line;
import nextstep.subway.api.domain.operators.LineFactory;
import nextstep.subway.api.infrastructure.persistence.LineRepository;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Component
@RequiredArgsConstructor
public class SimpleLineFactory implements LineFactory {

	private final LineRepository lineRepository;

	@Override
	public Line createLine(LineCreateCommand request) {
		return lineRepository.save(Line.from(request));
	}

	@Override
	public void deleteLine(Line line) {
		lineRepository.delete(line);
	}

}
