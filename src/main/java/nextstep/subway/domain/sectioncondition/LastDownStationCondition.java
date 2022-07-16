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
    public boolean matches(final Line line, final AddSectionRequest request) {
        final List<Station> stations = line.getStations();
        return isUpStationMatchesLastStation(line, request, stations) && isDownStationNotRegistered(request, stations);
    }

    private boolean isUpStationMatchesLastStation(final Line line, final AddSectionRequest request, final List<Station> stations) {
        return request.getUpStation().equals(line.getLastStation(stations));
    }

    private boolean isDownStationNotRegistered(final AddSectionRequest request, final List<Station> stations) {
        return !stations.contains(request.getDownStation());
    }

    @Override
    public void add(final Line line, final AddSectionRequest addSectionRequest) {
        line.addSection(createSection(line, addSectionRequest));
    }

    private Section createSection(final Line line, final AddSectionRequest addSectionRequest) {
        return new Section(line, addSectionRequest.getUpStation(), addSectionRequest.getDownStation(), addSectionRequest.getDistance());
    }

}
