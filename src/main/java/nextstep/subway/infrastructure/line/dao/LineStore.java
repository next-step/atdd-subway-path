package nextstep.subway.infrastructure.line.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import nextstep.subway.domain.line.LineCommand;
import nextstep.subway.domain.line.entity.Line;
import nextstep.subway.domain.line.entity.Section;
import nextstep.subway.domain.station.entity.Station;
import nextstep.subway.infrastructure.line.LineRepository;
import nextstep.subway.infrastructure.station.StationRepository;
import nextstep.subway.interfaces.line.dto.LineRequest;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LineStore {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public Section createSection(LineCommand.SectionAddCommand command) {
        return this.createSection(command.getUpStationId(), command.getDownStationId(), command.getDistance());
    }
    public Section createSection(LineRequest.Line lineRequest) {
        return this.createSection(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }
    private Section createSection(Long upStationId, Long downStationId, Long distance) {
        List<Station> stations = stationRepository.findAllById(List.of(upStationId, downStationId));
        Station upStation = stations.stream().filter(station -> Objects.equals(station.getId(), upStationId)).findFirst().orElseThrow(() -> new EntityNotFoundException("station_id: " + upStationId));
        Station downStation = stations.stream().filter(station -> Objects.equals(station.getId(), downStationId)).findFirst().orElseThrow(() -> new EntityNotFoundException("station_id: " + downStationId));
        return Section.of(upStation, downStation, distance);
    }

    public Line store(Line init) {
        return lineRepository.save(init);
    }

    public void remove(Line line) {
        lineRepository.delete(line);
    }


}
