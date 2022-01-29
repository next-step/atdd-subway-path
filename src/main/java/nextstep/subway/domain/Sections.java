package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionArgumentException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    public static final String NOT_LAST_SECTION = "마지막 구간이 아닙니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, this.sections.get(0).getUpStation());

        return stations;
    }

    public void remove(Station station) {
        Section lastSection = getLastSection();
        if (!lastSection.isLast(station)) {
            throw new IllegalSectionArgumentException(NOT_LAST_SECTION);
        }

        this.sections.remove(lastSection);
    }

    private Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }
}
