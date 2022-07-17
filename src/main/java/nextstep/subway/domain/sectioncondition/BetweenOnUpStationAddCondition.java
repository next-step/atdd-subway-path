package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
class BetweenOnUpStationAddCondition implements AddSectionCondition {

    @Override
    public boolean matches(Line line, final AddSectionRequest request) {
        if (!hasUpStationMatchedSection(line, request.getUpStation())) {
            return false;
        }

        return !line.containsStation(request.getDownStation());
    }

    private boolean hasUpStationMatchedSection(final Line line, final Station upStation) {
        return findUpStationMatchedSection(line.getSections(), upStation)
                .isPresent();
    }

    private Predicate<Section> isUpStationMatches(final Station upStation) {
        return v -> v.getUpStation().equals(upStation);
    }

    @Override
    public void addSection(Line line, final AddSectionRequest addSectionRequest) {
        final List<Section> sections = line.getSections();
        final Section section = findUpStationMatchedSection(sections, addSectionRequest.getUpStation())
                .orElseThrow(IllegalStateException::new);

        validateDistance(addSectionRequest, section.getDistance());
        updateSection(line, addSectionRequest, sections, section);
    }

    private Optional<Section> findUpStationMatchedSection(final List<Section> sections, final Station upStation) {
        return sections.stream()
                .filter(isUpStationMatches(upStation))
                .findFirst();
    }

    private void validateDistance(final AddSectionRequest request, final int sectionDistance) {
        if (request.getDistance() >= sectionDistance) {
            throw new IllegalArgumentException("distance must be less than sectionDistance");
        }
    }

    private void updateSection(final Line line, final AddSectionRequest request, final List<Section> sections, final Section section) {
        line.addSection(sections.indexOf(section) + 1, new Section(line, request.getDownStation(), section.getDownStation(), section.getDistance() - request.getDistance()));
        section.updateDownStationAndDistance(request.getDownStation(), request.getDistance());
    }

}
