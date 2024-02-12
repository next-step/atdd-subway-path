package nextstep.subway.section;

import nextstep.exception.BadRequestException;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow();
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );

        Section newSection = sectionRepository.save(new Section(upStation, downStation, sectionRequest.getDistance()));

        line.addSection(newSection);
    }

    @Transactional
    public void deleteSection(Long lineId, Long deleteStationId) {
        Line line = lineRepository.findById(lineId).orElseThrow();
        Station station = stationRepository.findById(deleteStationId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역 입니다.")
        );
        Long deleteSectionId = line.deleteSection(station);
        sectionRepository.deleteById(deleteSectionId);
    }
}
