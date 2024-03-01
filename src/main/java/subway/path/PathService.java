package subway.path;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.Section;
import subway.section.SectionRepository;
import subway.station.Station;
import subway.station.StationRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        List<Section> sections = sectionRepository.findAll();
        Station start = stationRepository.findById(source)
                .orElseThrow(() -> new EntityNotFoundException("해당 역이 존재하지 않습니다."));
        Station end = stationRepository.findById(target)
                .orElseThrow(() -> new EntityNotFoundException("해당 역이 존재하지 않습니다."));

        PathGenerator path = new PathGenerator(sections);
        return new PathResponse(path.getStations(start, end), path.getDistance(start, end));
    }
}
