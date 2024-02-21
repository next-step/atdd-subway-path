package nextstep.subway.line.domain;

import lombok.Getter;
import nextstep.subway.global.exception.InsufficientStationException;
import nextstep.subway.global.exception.StationNotMatchException;
import nextstep.subway.section.domain.Section;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
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
            throw new EntityNotFoundException("지하철역이 존재하지 않습니다.");
        }
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public List<Long> getStations() {
        List<Long> stationIds = getSections().stream()
                .map(section -> section.getUpStationId())
                .collect(Collectors.toList());

        stationIds.add(getDownStationId());

        return stationIds;
    }

    public void removeSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        sections.remove(section);
        return;
    }

    private Section getLastSection() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sections.size() - 1);
    }

    public void validateLastStation() {
        if (this.getDownStationIds().isEmpty()) {
            throw new InsufficientStationException();
        }
    }

    public void validateDownStationId(Long stationId) {
        if (this.getDownStationId() != stationId) {
            throw new StationNotMatchException();
        }
    }
}
