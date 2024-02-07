package nextstep.subway.api.domain.service.impl;

import static org.springframework.http.HttpStatus.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.api.domain.dto.inport.SectionCreateCommand;
import nextstep.subway.api.domain.dto.outport.SectionInfo;
import nextstep.subway.api.domain.model.entity.Line;
import nextstep.subway.api.domain.model.entity.Section;
import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.api.domain.operators.LineResolver;
import nextstep.subway.api.domain.operators.SectionFactory;
import nextstep.subway.api.domain.operators.StationResolver;
import nextstep.subway.api.domain.service.SectionService;
import nextstep.subway.common.exception.LineNotFoundException;
import nextstep.subway.common.exception.StationNotFoundException;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnhancedSectionService implements SectionService {
	private final LineResolver lineResolver;
	private final SectionFactory sectionFactory;
	private final StationResolver stationResolver;


	// 존재하는 섹션들 중에
	// 0) empty인 경우 -> 그냥 생성
	// 1) 상행과 상행이 같은 경우 -> 중간 삽입 케이스
	// 2) 하행과 하행이 같은 경우 -> 중간 삽입 케이스
	// 3) command의 하행과 존재하는 최상단 구간의 상행이 같은 경우 -> 최상단 추가 케이스
	// 4) command의 상행과 존재하는 최하단 구간의 하행이 같은 경우 -> 최하단 추가 케이스
	// 5) 상행 하행이 모두 같은 경우 -> 예외
	// 6) 아무 것도 같지 않은 경우 -> 예외
	// 이미 등록되어 있는 역은 노선에 등록될 수 없음 !!!
	@Override
	@Transactional
	public SectionInfo addSection(Long lineId, SectionCreateCommand createCommand) {
		Line line = lineResolver.fetchOptional(lineId).orElseThrow(() -> new LineNotFoundException(BAD_REQUEST));

		Station upStation = stationResolver.fetchOptional(createCommand.getUpStationId()).orElseThrow(() -> new StationNotFoundException(BAD_REQUEST));
		Station downStation = stationResolver.fetchOptional(createCommand.getDownStationId()).orElseThrow(() -> new StationNotFoundException(BAD_REQUEST));

		Section newSection = sectionFactory.createSection(createCommand, upStation, downStation);

		line.insertSection(newSection);

		return SectionInfo.from(newSection);
	}




	@Override
	@Transactional
	public void deleteSection(Long lineId, Long stationId) {
		Line line = lineResolver.fetchOptional(lineId).orElseThrow(() -> new LineNotFoundException(BAD_REQUEST));

		line.removeStation(stationId);




		// if (line.isNotDownEndStation(stationId) || line.isSectionCountBelowThreshold(1)) {
		// 	throw new SectionDeletionNotValidException();
		// }

	}
}
