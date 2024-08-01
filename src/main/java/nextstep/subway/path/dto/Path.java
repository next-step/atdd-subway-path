package nextstep.subway.path.dto;

import nextstep.subway.path.exception.PathException;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;

public class Path {
    private final List<Station> stationList;
    private final double weight;

    public Path(List<Station> stationList, double weight) {
        this.stationList = stationList;
        this.weight = weight;
    }

    public PathResponse createPathResponse() {
        if (stationList == null || stationList.isEmpty()) {
            throw new PathException(String.valueOf(PATH_NOT_FOUND));
        }

        List<StationResponse> stationResponseList = new ArrayList<>();
        for (Station station : stationList) {
            stationResponseList.add(new StationResponse(station.getId(), station.getName()));
        }

        return new PathResponse(stationResponseList, weight);
    }

    public List<Station> getStationList() {
        return stationList;
    }

    public double getWeight() {
        return weight;
    }


}
