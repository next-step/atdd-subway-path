package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    private final static int END_STATIONS_SIZE = 2;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections;

    public Sections() {
        this.sections = new ArrayList<>();
    }

    public Section add(Section section) {
        if (isEmptySections()) {
            sections.add(section);
            return section;
        }

        if (isNotEndDownStation(section.getUpStation())) {
            throw new IllegalArgumentException("등록할 구간의 상행역은 해당 노선 구간의 하행 종점역이어야 합니다.");
        }

        if (isAlreadyRegisteredStation(section.getDownStation())) {
            throw new IllegalArgumentException("등록할 구간의 하행역은 해당 노선 구간에 등록되어 있지 않은 역이어야 합니다.");
        }

        sections.add(section);
        return section;
    }

    public void delete(Station station) {
        if (hasOnlyEndStations()) {
            throw new IllegalArgumentException("노선 구간에 종점역만 존재하여 더 이상 구간을 삭제할 수 없습니다.");
        }

        if (isNotEndDownStation(station)) {
            throw new IllegalArgumentException("삭제할 구간은 하행종점역 구간이 아닙니다.");
        }

        sections.remove(getLastSection().orElseThrow(() -> new IllegalArgumentException("더 이상 삭제할 구간이 존재하지 않습니다.")));
    }

    private boolean isAlreadyRegisteredStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isExistAnyStation(station));
    }

    private boolean isNotEndDownStation(Station station) {
        return getEndDownStation().map(endDownStation -> !endDownStation.equals(station)).orElse(true);

    }

    private boolean isEmptySections() {
        return sections.isEmpty();
    }

    public List<Station> getFlatStations() {
        if (isEmptySections()) {
            return new ArrayList<>();
        }

        Set<Station> stations = new LinkedHashSet<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });

        return new ArrayList<>(stations);
    }

    private Optional<Station> getEndDownStation() {
        if (!getLastSection().isPresent()) {
            return Optional.empty();
        }

        return Optional.of(getLastSection().get().getDownStation());
    }


    private boolean hasOnlyEndStations() {
        return getFlatStations().size() == END_STATIONS_SIZE;
    }


    private Optional<Section> getLastSection() {
        if (isEmptySections()) { //Defensive
            Optional.empty();
        }

        int sectionLastIndex = sections.size() - 1;

        return Optional.of(sections.get(sectionLastIndex));
    }
}
