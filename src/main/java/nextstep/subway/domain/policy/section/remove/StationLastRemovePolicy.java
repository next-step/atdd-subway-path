package nextstep.subway.domain.policy.section.remove;

import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.policy.section.StationRemovePolicy;

public class StationLastRemovePolicy implements StationRemovePolicy {

    @Override
    public boolean isSatisfied(Sections sections, Station station) {
        return sections.size() > 1 && sections.equalLastStation(station);
    }

    @Override
    public void execute(Sections sections, Station station) {
        sections.removeLastStation();

    }
}
