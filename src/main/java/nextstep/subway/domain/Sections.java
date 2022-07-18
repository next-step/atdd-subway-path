package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.size() > 0) {
            if (sections.contains(section)) {
                throw new IllegalArgumentException("같은 구간이 있습니다.");
            }
        }
        sections.add(section);
    }

    public void deleteSection(Station station) {
        Section lastSection = sections.get(getLastIndex());

        if (!lastSection.matchDownStation(station)) {
            throw new IllegalArgumentException("하행 종점역만 삭제 가능합니다.");
        }

        sections.remove(getLastIndex());
    }

    public List<Station> getStations() {
        return getSortedSection()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public List<Section> getSections() {
        return getSortedSection()
                .collect(Collectors.toList());
    }

    private Stream<Section> getSortedSection() {
        return sections.stream()
                .sorted((s1, s2) -> s1.getUpStation().getName().compareTo(s2.getDownStation().getName()));
    }
}
