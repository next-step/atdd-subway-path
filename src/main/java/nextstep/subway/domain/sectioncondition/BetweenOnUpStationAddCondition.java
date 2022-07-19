package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class BetweenOnUpStationAddCondition implements SectionAddCondition {

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

    private boolean isUpStationMatches(final Station upStation, final Section section) {
        return section.getUpStation().equals(upStation);
    }

    @Override
    public void addSection(Line line, final AddSectionRequest addSectionRequest) {
        final List<Section> sections = line.getSections();
        final Section section = findUpStationMatchedSection(sections, addSectionRequest.getUpStation())
                .orElseThrow(IllegalStateException::new);

        validateDistance(addSectionRequest, section.getDistance());
        updateSection(line, addSectionRequest, section);
    }

    private Optional<Section> findUpStationMatchedSection(final List<Section> sections, final Station upStation) {
        return sections.stream()
                .filter(v -> isUpStationMatches(upStation, v))
                .findFirst();
    }

    private void validateDistance(final AddSectionRequest request, final int sectionDistance) {
        if (request.getDistance() >= sectionDistance) {
            throw new IllegalArgumentException("distance must be less than sectionDistance");
        }
    }

    private void updateSection(final Line line, final AddSectionRequest request, final Section section) {
        line.addSection(section, createSection(request, section));
        section.updateDownStationAndDistance(request.getDownStation(), request.getDistance());
    }

    private Section createSection(final AddSectionRequest request, final Section section) {
        return new Section(request.getDownStation(), section.getDownStation(), section.getDistance() - request.getDistance());
    }

}
