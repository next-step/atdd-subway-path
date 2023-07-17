package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.LineRepository;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.entity.StationRepository;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    @Transactional
    public void enroll(long lineId, SectionRequest request) {
        Line line = getLine(lineId);

        Section newSection = getSection(request, line);

        line.addSection(newSection);
    }

    @Transactional
    public void deleteSection(long lineId, long stationId) {
        Station deleteReqStation = getStation(stationId);

        Line line = getLine(lineId);

        line.removeSection(deleteReqStation);
    }

    private Station getStation(Long lineDto) {
        Station upStation = stationRepository.findById(lineDto)
                .orElseThrow(() -> new IllegalArgumentException(String.format("역이 존재하지 않습니다. id:%s", lineDto)));
        return upStation;
    }

    private Section getSection(SectionRequest request, Line line) {
        Station newSectionUpStation = getStation(request.getUpStationId());
        Station newSectionDownStation = getStation(request.getDownStationId());
        Section newSection = new Section(line, newSectionUpStation, newSectionDownStation, request.getDistance());
        return newSection;
    }

    private Line getLine(long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 호선입니다. 호선id:%s", lineId)));
    }
}
