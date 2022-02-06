package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionResponse implements Comparable<SectionResponse> {
    private final Long id;
    private final Station upStation;
    private final Station downStation;

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.upStation = section.getUpStation();
        this.downStation = section.getDownStation();
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    @Override
    public int compareTo(SectionResponse o) {
        if (o.getId() < id) {
            return 1;
        } else if (o.getId() > id) {
            return -1;
        }
        return 0;
    }
}
