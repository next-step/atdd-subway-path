package nextstep.subway.line.domain;

import lombok.Getter;
import nextstep.subway.section.domain.Section;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class Sections {

    @OneToMany
    @JoinColumn(name = "line_id")
    private final List<Section> sections = new ArrayList<>();

    public List<Long> getDownStationIds() {
        return sections.stream()
                    .map(Section::getDownStationId)
                    .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void deleteLastSection() {
        Section section = getLastSection();
        sections.remove(section);
    }

    public Long getUpStationId() {
        return sections.get(0).getUpStationId();
    }

    public Long getDownStationId() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public int getTotalDistance() {
        return sections.stream()
                    .mapToInt(Section::getDistance)
                    .sum();
    }

    public List<Long> getStations() {
        List<Long> stationIds = getSections().stream()
                .map(section -> section.getUpStationId())
                .collect(Collectors.toList());

        stationIds.add(getDownStationId());

        return stationIds;
    }

    public void removeSection() {
        if (sections.isEmpty()) {
            return;
        }

        sections.remove(sections.size() - 1);

        return;
    }

    private Section getLastSection() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sections.size() - 1);
    }
}
