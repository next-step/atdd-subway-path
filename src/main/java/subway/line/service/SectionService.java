package subway.line.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import subway.line.model.Section;
import subway.line.repository.SectionRepository;
import subway.station.model.Station;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;

    public List<Section> findByStation(Station sourceStation, Station targetStation) {
        List<Section> sectionsByStation = sectionRepository.findByUpStationOrDownStation(sourceStation, sourceStation);
        List<Section> sectionsByTargetStation = sectionRepository.findByUpStationOrDownStation(targetStation, targetStation);
        sectionsByStation.addAll(sectionsByTargetStation);
        return sectionsByStation;
    }
}
