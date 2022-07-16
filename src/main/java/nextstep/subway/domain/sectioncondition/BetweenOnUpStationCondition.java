package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class BetweenOnUpStationCondition implements SectionCondition {

    @Override
    public boolean isSatisfiedBy(Line line, final AddSectionRequest addSectionRequest) {
        final List<Section> sections = line.getSections();
        final Optional<Section> section = sections.stream()
                .filter(v -> v.getUpStation().equals(addSectionRequest.getUpStation()))
                .findFirst();

        if (section.isEmpty()) {
            return false;
        }

        // 현재 섹션이 마지막 섹션이 아님
        return !line.getStations().contains(addSectionRequest.getDownStation());
    }

    @Override
    public void add(Line line, final AddSectionRequest addSectionRequest) {

        final List<Section> sections = line.getSections();
        final Section section = sections.stream()
                .filter(v -> v.getUpStation().equals(addSectionRequest.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        final int sectionDistance = section.getDistance();
        if (addSectionRequest.getDistance() >= sectionDistance) {
            throw new IllegalArgumentException("distance must be less than sectionDistance");
        }

        line.addSection(sections.indexOf(section) + 1, new Section(line, addSectionRequest.getDownStation(), section.getDownStation(), sectionDistance - addSectionRequest.getDistance()));
        section.updateDownStationAndDistance(addSectionRequest.getDownStation(), addSectionRequest.getDistance());
    }

}
