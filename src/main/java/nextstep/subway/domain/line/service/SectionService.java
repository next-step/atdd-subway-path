package nextstep.subway.domain.line.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.domain.Section;
import nextstep.subway.domain.line.dto.request.CreateSectionRequest;
import nextstep.subway.domain.line.dto.response.SectionResponse;
import nextstep.subway.domain.line.repository.LineRepository;
import nextstep.subway.domain.line.repository.SectionRepository;
import nextstep.subway.domain.station.domain.Station;
import nextstep.subway.domain.station.repository.StationRepository;
import nextstep.subway.global.exception.GlobalException;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public SectionResponse createSection(Long lineId, CreateSectionRequest request) {

        Line line = lineRepository.getLineById(lineId);
        Section section = Section.create(
                line,
                getStation(request, "upStation"),
                getStation(request, "downStation"),
                request.getDistance()
        );
        Section savedSection = sectionRepository.save(section);
        return SectionResponse.from(savedSection);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.getLineById(lineId);

        if (!line.isStationDirectionEqual(stationId)) {
            throw new GlobalException("하행 종점역과 다릅니다. 하행 종점역만 삭제가 가능합니다.");
        }

        if (!line.hasMoreThanOne(stationId)) {
            throw new GlobalException("노선의 구간이 하나인 경우 삭제가 불가합니다.");
        }

        line.removeSections();
    }

    private Station getStation(CreateSectionRequest request, String type) {
        if (type.equals("upStation")) {
            return stationRepository.getById(request.getUpStationId());
        }
        return stationRepository.getById(request.getDownStationId());
    }
}
