package nextstep.subway.section;

import nextstep.subway.line.Line;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    public SectionService(SectionDao sectionDao,
                          LineRepository lineRepository,
                          StationRepository stationRepository) {
        this.sectionDao = sectionDao;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public SectionResponse createSection(long lineId, SectionRequest sectionRequest) {

        Line line = getLine(lineId);

        if (!line.isLastStation(getStation(sectionRequest.getDownStationId()))) {
            throw new IllegalStateException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다");
        }
        LineSections sectionsByLine = sectionDao.findSectionsByLine(line.getId());
        if(sectionsByLine.hasStation(sectionRequest.getDownStationId())) {
            throw new IllegalStateException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다");
        }

        Station downStation = getStation(sectionRequest.getDownStationId());
        Section section = new Section(getStation(sectionRequest.getUpStationId()), downStation, sectionRequest.getDistance(), line.getId());
        line.addSection(section);
        Section saved = sectionDao.save(section);

        return new SectionResponse(saved.getUpStationId(), saved.getDownStationId(), saved.getDistance());
    }

    public void deleteSection(long lineId, long stationId) {

        Line line = getLine(lineId);
        if(!line.isLastStation(getStation(stationId))) {
            throw new IllegalStateException("마지막 구간만 제거할 수 있다.");
        }

        LineSections sections = sectionDao.findSectionsByLine(lineId);
        if(sections.hasOnlyOneSection()) {
            throw new IllegalStateException("구간이 1개여서 역을 삭제할 수 없다");
        }

        Section section = sectionDao.findDownStation(stationId);
        sectionDao.deleteSection(section);

        line.changeDownStation(getStation(section.getUpStationId()));
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