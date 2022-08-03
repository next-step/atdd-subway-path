package nextstep.subway.domain;

import nextstep.subway.exception.AllStationsOfSectionExistException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        checkDuplicateSection(section);

        sections.add(section);
    }

    private void checkDuplicateSection(Section section) {
        sections.stream()
                .filter(it -> it.hasDuplicateSection(section.getUpStation(), section.getDownStation()))
                .findFirst()
                .ifPresent(it -> {
                    throw new AllStationsOfSectionExistException("신규 구간의 역이 이미 존재합니다.");
                });
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public Station getLastStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public void removeSection(Long stationId) {
        sections.removeIf(section -> section.getDownStation().compare(stationId));
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
