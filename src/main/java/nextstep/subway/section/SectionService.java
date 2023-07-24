package nextstep.subway.section;

import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.StationRepository;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

@Service
@Transactional
public class SectionService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public SectionService(StationRepository stationRepository, LineRepository lineRepository, SectionRepository sectionRepository) {
      this.stationRepository = stationRepository;
      this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public void createSection(final long lineId, final SectionRequest request) {
      final var line = lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException(lineId));

      final var upStation = getStationById(request.getUpStationId());
      final var downStation = getStationById(request.getDownStationId());

      sectionRepository.saveAll(line.addSection(upStation, downStation, request.getDistance()));
    }

    private Station getStationById(final Long stationId) {
      return stationRepository.findById(stationId)
              .orElseThrow(() -> new StationNotFoundException(stationId));
    }

}
