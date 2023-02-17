package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

@Component
public class StationMapper {

    public List<StationResponse> toResponseFrom(final List<Station> stations) {
        return stations.stream()
                .map(this::toResponseFrom)
                .collect(Collectors.toUnmodifiableList());
    }

    public StationResponse toResponseFrom(final Station stations) {
        return new StationResponse(stations.getId(), stations.getName());
    }
}
