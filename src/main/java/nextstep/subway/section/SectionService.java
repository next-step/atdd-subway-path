package nextstep.subway.section;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;

@Service
@RequiredArgsConstructor
public class SectionService {

	private final LineRepository lineRepository;
	private final SectionRepository sectionRepository;
	private final StationRepository stationRepository;

	@Transactional
	public void addSection(Long lineId, SectionCreateRequest sectionRequest) {
		Line findLine = lineRepository.findById(lineId).orElseThrow(() -> new NullPointerException("Line doesn't exist"));
		Section newSection = saveSection(sectionRequest);

		findLine.addSection(newSection);
		newSection.updateLine(findLine);
	}

	@Transactional
	public Section saveSection(SectionCreateRequest sectionRequest) {
		Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new NullPointerException("Station doesn't exist"));
		Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new NullPointerException("Station doesn't exist"));

		Section newSection = new Section(upStation, downStation, sectionRequest.getDistance());
		return sectionRepository.save(newSection);
	}

	@Transactional
	public void deleteSectionById(Long lineId, Long downStationId) {
		Line line = lineRepository.findById(lineId).orElseThrow(() -> new NullPointerException("Line doesn't exist"));
		Section findSection = sectionRepository.findByDownStation_Id(downStationId);

		line.removeSection(findSection.getDownStation());
	}
}
