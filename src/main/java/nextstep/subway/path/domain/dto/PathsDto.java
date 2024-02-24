package nextstep.subway.path.domain.dto;

import nextstep.subway.station.domain.Station;

import java.math.BigDecimal;
import java.util.List;

public class PathsDto {

    private double weight;
    private List<Station> paths;

    public PathsDto(double weight, List<Station> paths) {
        this.weight = weight;
        this.paths = paths;
    }

    public int getDistance() {
        return new BigDecimal(weight).intValue();
    }

    public List<Station> getPaths() {
        return paths;
    }
}
