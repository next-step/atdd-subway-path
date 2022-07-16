package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
class BetweenOnDownStationCondition implements SectionCondition {

    @Override
    public boolean isSatisfiedBy(Line line, final AddSectionRequest request) {
        if (!hasRequestDownStationMatchedSection(line, request.getDownStation())) {
            return false;
        }

        return isRequestUpStationNotRegistered(line, request.getUpStation());
    }

    private boolean hasRequestDownStationMatchedSection(final Line line, final Station downStation) {
        return line.getSections().stream()
                .anyMatch(isDownStationMatches(downStation));
    }

    private Predicate<Section> isDownStationMatches(final Station downStation) {
        return v -> v.getDownStation().equals(downStation);
    }

    private boolean isRequestUpStationNotRegistered(final Line line, final Station upStation) {
        return !line.getStations().contains(upStation);
    }

    @Override
    public void add(final Line line, final AddSectionRequest request) {
        final List<Section> sections = line.getSections();
        final Section section = findDownStationMatchedSection(sections, request.getDownStation());

        validateSectionLength(request, section.getDistance());
        updateSection(line, request, sections, section);
    }

    private void updateSection(final Line line, final AddSectionRequest addSectionRequest, final List<Section> sections, final Section section) {
        section.updateDownStationAndDistance(addSectionRequest.getUpStation(), section.getDistance() - addSectionRequest.getDistance());
        line.addSection(sections.indexOf(section) + 1, createSection(line, addSectionRequest));
    }

    private Section createSection(final Line line, final AddSectionRequest addSectionRequest) {
        return new Section(line, addSectionRequest.getUpStation(), addSectionRequest.getDownStation(), addSectionRequest.getDistance());
    }

    private void validateSectionLength(final AddSectionRequest addSectionRequest, final int sectionDistance) {
        if (addSectionRequest.getDistance() >= sectionDistance) {
            throw new IllegalArgumentException("distance must be less than sectionDistance");
        }
    }

    private Section findDownStationMatchedSection(final List<Section> sections, final Station downStation) {
        return sections.stream()
                .filter(isDownStationMatches(downStation))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}
