package nextstep.subway.section.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public void addSection(Long lineId, SectionDto sectionDto) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new SubwayException(ErrorCode.BAD_REQUEST));

        // 신규 구간의 상행역
        Station upStation = stationRepository.findById(sectionDto.getUpStationId())
                .orElseThrow(() -> new SubwayException(ErrorCode.BAD_REQUEST));
        // 신규 구간의 하행역
        Station downStation = stationRepository.findById(sectionDto.getDownStationId())
                .orElseThrow(() -> new SubwayException(ErrorCode.BAD_REQUEST));

        // 신귶 구간 등록
        line.addSection(sectionDto.toEntity(line, upStation, downStation));
    }

    public void removeSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new SubwayException(ErrorCode.BAD_REQUEST));

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new SubwayException(ErrorCode.BAD_REQUEST));

        line.removeSection(station);
    }
}
