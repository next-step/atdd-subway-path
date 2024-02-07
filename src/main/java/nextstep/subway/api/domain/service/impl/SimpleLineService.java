package nextstep.subway.api.domain.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.api.domain.dto.inport.LineCreateCommand;
import nextstep.subway.api.domain.dto.inport.LineUpdateCommand;
import nextstep.subway.api.domain.model.entity.Line;
import nextstep.subway.api.domain.model.entity.Section;
import nextstep.subway.api.domain.operators.LineFactory;
import nextstep.subway.api.domain.operators.LineResolver;
import nextstep.subway.api.domain.operators.SectionFactory;
import nextstep.subway.api.domain.service.LineService;
import nextstep.subway.api.interfaces.dto.response.LineResponse;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SimpleLineService implements LineService {

	private final LineFactory lineFactory;
	private final SectionFactory sectionFactory;
	private final LineResolver lineResolver;

	/**
	 * 기존의 방식은 Line을 먼저 만들고 Section을 생성
	 * 현재 방식은 Section을 만들고 Line 생성 후, Line에 만들었던 Section을 추가하는 시점에
	 * 해당 Section에 Line을 매핑해준다.
	 * <p>
	 * 현재 방식에서의 단점은 Section 생성시 Line이 nullable을 허용해야 한다는 점이다.
	 * 장점은, 현실세계의 논리적 관계(노선보다 구간이 선행함)를 반영하며, 현재 구현에서 Section 생성시에는 Station이 없는 경우에 대한 예외 처리를 하고 있으므로 Section의 생성은 보장된다는 점이다.
	 * 즉, 이는 도메인 규칙에 따라 항상 노선이 하나 이상의 구간을 포함함을 보장한다.
	 * <p>
	 * 기존 방식의 경우에도 트랜잭션으로 묶여있으므로 생성 로직 자체에서 노선이 구간을 포함하지 않으면서 생성될 수는 없다.
	 * <p>
	 * 노선 생성 방식에 있어서 다른 방식으로 접근해볼 수 있는 점은, 현재와 같이 Line, Section, Station을 각각의 Factory로 분리하지 않고
	 * 하나의 클래스로 통합하는 것이다. 그렇게 하면 saveLineWithInitialize()의 의미를 구현하는 통합 save 로직을 구현할 수 있다.
	 * 예를 들어 station을 먼저 조회하고 검증하며, Station으로 Section을 생성하고, 이후 Line을 만든다.
	 * <p>
	 * 가급적이면 최소 단위의 범위를 최소화하는 걸 선호하는 편이어서 현재의 구분 방식으로 구현했다.
	 *
	 * @param command
	 * @return
	 */
	@Override
	@Transactional
	public LineResponse saveLine(LineCreateCommand command) {
		Section section = sectionFactory.createSection(command);
		Line line = lineFactory.createLine(command);
		line.addSection(section);

		return LineResponse.from(line);
	}

	@Override
	public List<LineResponse> findAllLines() {
		return lineResolver.fetchAll().stream().map(LineResponse::from).collect(Collectors.toList());
	}

	@Override
	public LineResponse findLineById(Long id) {
		return LineResponse.from(fetchLineOrThrow(id));
	}

	@Override
	@Transactional
	public LineResponse updateLineById(Long id, LineUpdateCommand updateRequest) {
		Line line = fetchLineOrThrow(id);

		line
			.updateName(updateRequest.getName())
			.updateColor(updateRequest.getColor());

		return LineResponse.from(line);
	}

	@Override
	@Transactional
	public void deleteLineById(Long id) {
		Line line = fetchLineOrThrow(id);

		sectionFactory.deleteByLine(line);
		lineFactory.deleteLine(line);
	}

	private Line fetchLineOrThrow(Long id) {
		return lineResolver.fetchOptional(id).orElseThrow(EntityNotFoundException::new);
	}

}
