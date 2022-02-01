package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                .anyMatch(it -> it.isUpStation(station) || it.isDownStation(station));
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

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        // 상행 종점 찾기
        final List<Station> stations = new ArrayList<>();
        final Station endUpStation = findEndUpStation();
        stations.add(endUpStation);

        // 다음 구간 찾기 -> 하행 종점까지 찾는다.
        Optional<Station> nextSection = findNextSection(endUpStation);
        while (nextSection.isPresent()) {
            final Station downStation = nextSection.get();
            stations.add(downStation);
            nextSection = findNextSection(downStation);
        }
        return Collections.unmodifiableList(stations);
    }

    private Optional<Station> findNextSection(final Station station) {
        return sections.stream()
                .filter(it -> it.isUpStation(station))
                .map(Section::getDownStation)
                .findAny();
    }

    private Station findEndUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(this::isNotDownStation)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("no have end upStation"));
    }

    // Predicate.not 으로 처리하려는 11버전 모듈로 사용해서 ! 사용 메서드로 변경
    private boolean isNotDownStation(final Station station) {
        return sections.stream()
                .noneMatch(it -> it.isDownStation(station));
    }
}
