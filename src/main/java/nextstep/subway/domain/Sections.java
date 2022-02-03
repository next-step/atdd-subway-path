package nextstep.subway.domain;

import nextstep.subway.exception.IllegalUpdatingStateException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    public static final int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Section addSection(Section section) {
        checkPossibleAddingSection(section);
        sections.add(section);
        return section;
    }

    private void checkPossibleAddingSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }
        if (!Objects.equals(getLastDownStation(), section.getUpStation())) {
            throw new IllegalUpdatingStateException("마지막 하행선이 요청한 구간의 상행선과 동일하지 않아 구간을 추가하지 못합니다.");
        }
        if (getAllStations().contains(section.getDownStation())) {
            throw new IllegalUpdatingStateException("요청한 구간의 하행역이 이미 노선에 등록되어있습니다.");
        }
    }

    public List<Station> getAllStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> allStations = new ArrayList<>();
        allStations.add(sections.get(0).getUpStation());
        allStations.addAll(sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));
        return allStations;
    }

    private Station getLastDownStation() {
        if (sections.isEmpty()) {
            return null;
        }
        return sections.get(sections.size() - 1).getDownStation();
    }

    public Section removeSection(Station station) {
        checkPossibleRemovingSection(station);
        return sections.remove(sections.size() - 1);
    }

    private void checkPossibleRemovingSection(Station station) {
        if (sections.size() <= MIN_SECTION_SIZE) {
            throw new IllegalUpdatingStateException("해당 노선의 구간이 " + MIN_SECTION_SIZE + "개 이하라 삭제하지 못합니다.");
        }
        if (!Objects.equals(getLastDownStation(), station)) {
            throw new IllegalUpdatingStateException("해당 노선의 하행 종점역이 아니라 삭제하지 못합니다.");
        }
    }
}
