package nextstep.subway.section;

import nextstep.subway.Station;
import nextstep.subway.StationNotFoundException;
import nextstep.subway.StationRepository;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineNotFoundException;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineStationRepository;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

@Service
@Transactional
public class SectionService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;

    private static final long MINIMUM_STATION_COUNT = 2L;

    public SectionService(StationRepository stationRepository, LineRepository lineRepository, LineStationRepository lineStationRepository) {
      this.stationRepository = stationRepository;
      this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
    }

    public void createSection(final long lineId, final SectionRequest request) {
      final var line = lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException(lineId));

      final var upStation = getStationById(request.getUpStationId());
      final var downStation = getStationById(request.getDownStationId());

      if (!line.isLastStation(upStation)) {
        throw new StationDoesNotMatchException(upStation.getName());
      }

      lineStationRepository.save(line.addSection(downStation, request.getDistance()));
    }

    private Station getStationById(final Long stationId) {
      return stationRepository.findById(stationId)
              .orElseThrow(() -> new StationNotFoundException(stationId));
    }

    public void deleteSection(Long lineId, Long stationId) {
        final var line = lineRepository.findById(lineId).orElseThrow(() -> new LineNotFoundException(lineId));
        final var station = getStationById(stationId);

        if (!line.isLastStation(station)) {
          throw new StationDoesNotMatchException(station.getName());
        }

        if (hasMinimumSection(line)) {
          throw new IllegalArgumentException("구간은 최소 하나입니다");
        }

        lineStationRepository.delete(line.removeSection(station));
    }

    private static boolean hasMinimumSection(Line line) {
        return line.countOfStations() == MINIMUM_STATION_COUNT;
    }
}
