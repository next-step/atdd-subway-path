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

    private static final int FIRST_POSITION = 0;
    private static final int GAP_SIZE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections;

    public Sections() {
        this(new ArrayList<>());
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(final Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        // 둘다 있거나 없을 경우
        validateStations(section);
        sections.add(section);
    }

    private void validateStations(final Section section) {
        final boolean upStationExistence = findStationExistence(section.getUpStation());
        final boolean downStationExistence = findStationExistence(section.getDownStation());
        if ((upStationExistence && downStationExistence) || (!upStationExistence && !downStationExistence)) {
            throw new IllegalArgumentException("station is not valid");
        }
    }

    private boolean findStationExistence(final Station station) {
        return sections.stream()
                .anyMatch(it -> it.hasAnyStation(station));
    }

    public void removeSection(final Station station) {
        validateRemoveSection(station);
        this.sections.remove(lastIndex());
    }

    private void validateRemoveSection(final Station station) {
        if (!sections.get(sections.size() - GAP_SIZE).getDownStation().equals(station)) {
            throw new IllegalArgumentException("invalid remove section");
        }
    }

    private int lastIndex() {
        return sections.size() - GAP_SIZE;
    }

    // Stations 는 공수비용 + 만들 이유 없어서 일급 컬렉션화 하지 않았습니다.
    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        // 상행 종점 찾기
        sections.stream()
                .map(Section::getUpStation)
                .filter(this::isDownStation)

        final List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(FIRST_POSITION, sections.get(FIRST_POSITION).getUpStation());
        return Collections.unmodifiableList(stations);
    }

    private boolean isDownStation(final Station station) {
        return sections.stream()
                .anyMatch(it -> it.isDonwStation(station));
    }
}
