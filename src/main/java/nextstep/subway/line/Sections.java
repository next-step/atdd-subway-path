package nextstep.subway.line;


import nextstep.subway.section.Section;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(final Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public void checkLineStationsDuplicate(final Station downStation) {
        for (Section section : this.sections) {
            section.checkEqualsUpStation(downStation);
        }
    }

    public int count() {
        return this.sections.size();
    }

    public void removeSection(final Long stationId) {
        final Section deleteSection = this.sections.stream()
                .filter(s -> s.getDownStation().isSameId(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("역을 찾을 수 없습니다."));

        this.sections.remove(deleteSection);
    }
}
