package nextstep.subway.domain.policy.section;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

public interface StationRemovePolicy {
    boolean isSatisfied(Sections sections, Station station);

    void execute(Sections sections, Station station);

}
