package nextstep.subway.domain;

import nextstep.subway.domain.exception.IllegalAddSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Line line, Station requestUpStation, Station requestDownStation, int requestDistance) {
        if (sections.isEmpty()) {
            sections.add(new Section(line, requestUpStation, requestDownStation, requestDistance));
            return;
        }

        Section section = getSectionToAdd(requestUpStation, requestDownStation);

        sections.add(section.makeNext(line, requestUpStation, requestDownStation, requestDistance));

        if (canAddInTheMiddleStation(requestUpStation, section)) {
            section.changeUpStation(requestDownStation, requestDistance);
        }
    }

    public List<Station> getOrderedStations() {
        return sections.stream().sorted()
            .map(section -> List.of(section.getUpStation(), section.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void removeLastSection(Station station) {
        if (!isLastStation(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(sections.size() - 1);
    }

    public boolean isSectionsEmpty() {
        return sections.isEmpty();
    }

    private Section getSectionToAdd(Station requestUpStation, Station requestDownStation) {
        return sections.stream().filter(section -> isPossibleToAddSection(requestUpStation, requestDownStation, section))
            .findFirst()
            .orElseThrow(IllegalAddSectionException::new);
    }

    private static boolean isPossibleToAddSection(Station requestUpStation, Station requestDownStation, Section section) {
        // 상행역 또는 하행역에 추가
        if (canAddUpOrDownStation(requestUpStation, requestDownStation, section)) {
            return true;
        }

        // 상행역 하행역 사이 추가
        return canAddInTheMiddleStation(requestUpStation, section);
    }

    private static boolean canAddUpOrDownStation(Station requestUpStation, Station requestDownStation, Section section) {
        return section.getUpStation().equals(requestDownStation) || section.getDownStation().equals(requestUpStation);
    }

    private static boolean canAddInTheMiddleStation(Station requestUpStation, Section section) {
        return section.getUpStation().equals(requestUpStation);
    }

    private boolean isLastStation(Station station) {
        return sections.get(sections.size() - 1).getDownStation().equals(station);
    }

}
