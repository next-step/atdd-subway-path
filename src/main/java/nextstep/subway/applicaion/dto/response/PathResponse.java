package nextstep.subway.applicaion.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.PathDto;
import nextstep.subway.applicaion.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public static PathResponse from(PathDto pathDto) {
        List<StationResponse> stations = pathDto.getShortestPath()
                                               .stream()
                                               .map(StationDto::from)
                                               .map(StationResponse::from)
                                               .collect(Collectors.toList());

        return new PathResponse(stations, pathDto.getShortestDistance());
    }

}
