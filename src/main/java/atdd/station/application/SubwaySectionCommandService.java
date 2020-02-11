package atdd.station.application;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwaySection;
import atdd.station.domain.SubwaySectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SubwaySectionCommandService {
    private SubwayLineQueryService subwayLineQueryService;
    private StationQueryService stationQueryService;
    private SubwaySectionRepository subwaySectionRepository;

    public SubwaySectionCommandService(SubwayLineQueryService subwayLineQueryService,
                                       StationQueryService stationQueryService,
                                       SubwaySectionRepository subwaySectionRepository) {

        this.subwayLineQueryService = subwayLineQueryService;
        this.stationQueryService = stationQueryService;
        this.subwaySectionRepository = subwaySectionRepository;
    }

    @Transactional
    public SubwaySection createSubwaySection(Long subwayLineId, Long sourceStationId, Long targetStationId) {

        SubwayLine savedSubwayLine = subwayLineQueryService.findSubwayLineById(subwayLineId);

        Station sourceStation = stationQueryService.findStationById(sourceStationId);
        Station targetStation = stationQueryService.findStationById(targetStationId);

        SubwaySection newSubwaySection = SubwaySection.of(savedSubwayLine, sourceStation, targetStation);

        return subwaySectionRepository.save(newSubwaySection);
    }

    @Transactional
    public void deleteStationOfSubwaySection(Long subwayLineId, String stationName) {

        SubwayLine savedSubwayLine = subwayLineQueryService.findSubwayLineById(subwayLineId);
        Station savedStation = stationQueryService.findStationByName(stationName);

        List<SubwaySection> savedSubwaySections = subwaySectionRepository.findAllBySubwayLineAndSourceStationOrTargetStation(savedSubwayLine, savedStation, savedStation);

        List<Station> stations = savedSubwaySections.stream()
                .flatMap(subwaySection -> Stream.of(subwaySection.getSourceStation(), subwaySection.getTargetStation()))
                .filter(station -> station.isNotMatchBy(savedStation))
                .collect(Collectors.toList());

        subwaySectionRepository.deleteAllBySourceStationOrTargetStation(savedStation, savedStation);

        if (stations.size() > 1) {
            subwaySectionRepository.save(SubwaySection.of(savedSubwayLine, stations.get(0), stations.get(1)));
        }
    }
}
