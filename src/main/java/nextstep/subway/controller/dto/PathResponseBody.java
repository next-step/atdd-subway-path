package nextstep.subway.controller.dto;

import nextstep.subway.service.dto.PathDto;

import java.util.List;

public class PathResponseBody {
    List<StationResponseBody> stations;
    int distance;

    private PathResponseBody(List<StationResponseBody> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponseBody> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public static PathResponseBody create(PathDto pathDto) {
        return new PathResponseBody(
                StationResponseBody.create(pathDto.getStations()),
                pathDto.getDistance());
    }
}
