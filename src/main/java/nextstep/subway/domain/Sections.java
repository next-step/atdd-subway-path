package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int ALL_MATCH_COUNT = 2;
    private static final int NONE_MATCH_COUNT = 0;
    private static final String ALL_MATCH_SECTION_ERROR_MESSAGE = "상행역과 하행역이 모두 등록된 상태입니다.";
    private static final String NONE_MATCH_SECTION_ERROR_MESSAGE = "상행역과 하행역이 모두 등록되지 않았습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() { }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        validationSection(section);

        int addIndex = existingSectionIndex(section);
        if (addIndex > -1) {
            sections.get(addIndex).changeDistance(section);
            sections.add(addIndex, section);
            return;
        }

        sections.add(section);
    }

    private int existingSectionIndex(Section section) {
        Section findSection = sections.stream()
                .filter(sec -> sec.anyMatchUpStation(section))
                .findFirst()
                .orElse(new Section());

        return sections.indexOf(findSection);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, sections.get(0).getUpStation());

        return Collections.unmodifiableList(stations);
    }

    public void deleteSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    private void validationSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        if (allMatchStation(section)) {
            throw new IllegalArgumentException(ALL_MATCH_SECTION_ERROR_MESSAGE);
        }

        if (noneMatchStation(section)) {
            throw new IllegalArgumentException(NONE_MATCH_SECTION_ERROR_MESSAGE);
        }
    }

    private boolean allMatchStation(Section section) {
        return sections.stream()
                .filter(sec -> sec.matchStation(section))
                .count() == ALL_MATCH_COUNT;
    }

    private boolean noneMatchStation(Section section) {
        return sections.stream()
                .filter(sec -> sec.matchStation(section))
                .count() == NONE_MATCH_COUNT;
    }
}
