package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class AddSectionRequest {
    private Station upStation;
    private Station downStation;
    private int distance;

    public AddSectionRequest(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public Section toSection() {
        return new Section(upStation, downStation, distance);
    }

    public Section toSection(final int distance) {
        return new Section(upStation, downStation, distance);
    }

}
