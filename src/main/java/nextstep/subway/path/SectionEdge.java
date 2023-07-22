package nextstep.subway.path;

import lombok.AllArgsConstructor;

import nextstep.subway.station.domain.Station;

@AllArgsConstructor
public class SectionEdge implements Edge<Station> {
    Station upStation;
    Station downStation;

    @Override
    public Station getSource() {
        return upStation;
    }

    @Override
    public Station getTarget() {
        return downStation;
    }
}
