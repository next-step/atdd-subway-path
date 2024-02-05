package nextstep.subway.line.section;

import nextstep.subway.station.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;

import java.util.List;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Section addSection(long lineId, SectionRequest request) {
        Line line = getLine(lineId);
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        return this.sectionRepository.save(new Section(upStation, downStation, request.getDistance(), line));
    }

    private Line getLine(long id) {
        return this.lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    private Station getStation(long id) {
        return this.stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }


    public Section getSection(long id) {
        return this.sectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구간입니다."));
    }

    public List<Section> getSections() {
        return this.sectionRepository.findAll();
    }


    public void deleteSection(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Section section = this.sectionRepository.findByDownStationId(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구간입니다."));
        line.deleteSection(section);
    }

}
