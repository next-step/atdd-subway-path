package nextstep.subway.section.domain;

import lombok.Getter;

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

    public void addSection(Section section) {
        sections.add(section);
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
}
