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
class BetweenOnDownStationAddCondition implements AddSectionCondition {

    @Override
    public boolean matches(Line line, final AddSectionRequest request) {
        if (!hasDownStationMatchedSection(line, request.getDownStation())) {
            return false;
        }

        return !line.containsStation(request.getUpStation());
    }

    private boolean hasDownStationMatchedSection(final Line line, final Station downStation) {
        return findDownStationMatchedSection(line.getSections(), downStation)
                .isPresent();
    }

    private Predicate<Section> isDownStationMatches(final Station downStation) {
        return v -> v.getDownStation().equals(downStation);
    }

    @Override
    public void addSection(final Line line, final AddSectionRequest request) {
        final List<Section> sections = line.getSections();
        final Section section = findDownStationMatchedSection(sections, request.getDownStation())
                .orElseThrow(IllegalStateException::new);

        validateDistance(request, section.getDistance());
        updateSection(line, request, section);
    }

    private Optional<Section> findDownStationMatchedSection(final List<Section> sections, final Station downStation) {
        return sections.stream()
                .filter(isDownStationMatches(downStation))
                .findFirst();
    }

    private void validateDistance(final AddSectionRequest request, final int sectionDistance) {
        if (request.getDistance() >= sectionDistance) {
            throw new IllegalArgumentException("distance must be less than sectionDistance");
        }
    }

    private void updateSection(final Line line, final AddSectionRequest request, final Section section) {
        line.addSection(section, request.toSection());
        section.updateDownStationAndDistance(request.getUpStation(), section.getDistance() - request.getDistance());
    }

}
