package nextstep.subway.line;

import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    public SectionService(LineRepository lineRepository,
                          StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public SectionResponse createSection(long lineId, SectionRequest sectionRequest) {

        Line line = getLine(lineId);

        if (!line.isLastStation(sectionRequest.getUpStationId())) {
            throw new IllegalStateException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다");
        }

        if(line.hasStation(sectionRequest.getDownStationId())){
            throw new IllegalStateException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다");
        }

        Station downStation = getStation(sectionRequest.getDownStationId());
        Station upStation = getStation(sectionRequest.getUpStationId());
        Section section = new Section(upStation, downStation, sectionRequest.getDistance(), line);
        line.addSection(section);
        Line saved = lineRepository.save(line);

        return new SectionResponse(saved.getUpStation().getId(), saved.getDownStation().getId(), saved.getDistance());
    }

    public void deleteSection(long lineId, long stationId) {

        Line line = getLine(lineId);
        line.removeSection(stationId);

        if(line.deletableSection()) {
            throw new IllegalStateException("구간이 1개여서 역을 삭제할 수 없다");
        }

        line.removeSection(stationId);
        lineRepository.save(line);
    }

    private Line getLine(long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(lineId));
    }

    private Station getStation(long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(stationId));
    }
}