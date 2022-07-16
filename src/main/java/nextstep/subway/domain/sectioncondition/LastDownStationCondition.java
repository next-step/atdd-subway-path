package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class LastDownStationCondition implements SectionCondition {

    @Override
    public boolean isSatisfiedBy(final Line line, final AddSectionRequest addSectionRequest) {
        final List<Station> stations = line.getStations();
        return addSectionRequest.getUpStation().equals(line.getLastStation(stations)) && !stations.contains(addSectionRequest.getDownStation());
    }

    @Override
    public void add(final Line line, final AddSectionRequest addSectionRequest) {
        line.addSection(new Section(line, addSectionRequest.getUpStation(), addSectionRequest.getDownStation(), addSectionRequest.getDistance()));
    }

}
