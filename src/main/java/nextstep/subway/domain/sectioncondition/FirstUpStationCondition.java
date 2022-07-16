package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class FirstUpStationCondition implements SectionCondition {

    @Override
    public boolean matches(final Line line, final AddSectionRequest request) {
        final List<Station> stations = line.getStations();
        return isDownStationMatchesFirstStation(line, request, stations) && isUpStationNotRegistered(request, stations);
    }

    private boolean isDownStationMatchesFirstStation(final Line line, final AddSectionRequest request, final List<Station> stations) {
        return request.getDownStation().equals(line.getFirstStation(stations));
    }

    private boolean isUpStationNotRegistered(final AddSectionRequest request, final List<Station> stations) {
        return !stations.contains(request.getUpStation());
    }

    @Override
    public void add(final Line line, final AddSectionRequest request) {
        line.addSection(0, createSection(line, request));
    }

    private Section createSection(final Line line, final AddSectionRequest request) {
        return new Section(line, request.getUpStation(), request.getDownStation(), request.getDistance());
    }

}
