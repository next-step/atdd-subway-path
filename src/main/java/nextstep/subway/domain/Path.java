package nextstep.subway.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Path {

    private List<Station> stations;
    private int distance;
}
