package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class BetweenOnUpStationCondition implements SectionCondition {

    @Override
    public boolean matches(Line line, final AddSectionRequest request) {
        if (!hasRequestUpStationMatchedSection(line, request)) {
            return false;
        }

        return isRequestDownStationNotRegistered(line, request.getDownStation());
    }

    private boolean hasRequestUpStationMatchedSection(final Line line, final AddSectionRequest request) {
        return line.getSections().stream()
                .anyMatch(v -> isUpStationMatches(v, request.getUpStation()));
    }

    private boolean isUpStationMatches(final Section v, final Station upStation) {
        return v.getUpStation().equals(upStation);
    }

    private boolean isRequestDownStationNotRegistered(final Line line, final Station downStation) {
        return !line.getStations().contains(downStation);
    }

    @Override
    public void add(Line line, final AddSectionRequest addSectionRequest) {
        final List<Section> sections = line.getSections();
        final Section section = findUpStationMatchedSection(sections, addSectionRequest.getUpStation());

        validateDistance(addSectionRequest, section.getDistance());
        updateSection(line, addSectionRequest, sections, section);
    }

    private void updateSection(final Line line, final AddSectionRequest request, final List<Section> sections, final Section section) {
        line.addSection(sections.indexOf(section) + 1, new Section(line, request.getDownStation(), section.getDownStation(), section.getDistance() - request.getDistance()));
        section.updateDownStationAndDistance(request.getDownStation(), request.getDistance());
    }

    private Section findUpStationMatchedSection(final List<Section> sections, final Station upStation) {
        return sections.stream()
                .filter(v -> isUpStationMatches(v, upStation))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private void validateDistance(final AddSectionRequest request, final int sectionDistance) {
        if (request.getDistance() >= sectionDistance) {
            throw new IllegalArgumentException("distance must be less than sectionDistance");
        }
    }

}
