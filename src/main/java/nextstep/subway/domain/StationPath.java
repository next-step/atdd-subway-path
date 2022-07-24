package nextstep.subway.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class StationPath {

    private final List<Station> stations;
    private final int distance;

}
