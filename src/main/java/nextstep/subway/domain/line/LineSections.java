package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.line.exception.LineRemoveSectionException;
import nextstep.subway.domain.line.exception.LineSectionsEmptyException;
import nextstep.subway.domain.station.Station;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LineSections {
    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "line_section",
            joinColumns = @JoinColumn(name = "line.id"),
            inverseJoinColumns = @JoinColumn(name = "section.id")
    )
    private final List<Section> value = new ArrayList<>();

    private static final int MINIMUM_SECTION_SIZE = 1;

    public static LineSections init(final Section section) {
        final var sections = new LineSections();
        sections.value.add(section);
        return sections;
    }

    public void append(final LineSectionAppender sectionAppender, final Section section) {
        sectionAppender.append(this, section);
    }

    public void remove(final Station station) {
        requireStationRemovable(station);
        value.remove(getLastSection());
    }

    private void requireStationRemovable(final Station station) {
        if (!getLastStation().equalsId(station)) {
            throw new LineRemoveSectionException("노선의 하행역이 아닙니다 : 역 id=" + station.getId());
        }
        if (value.size() == MINIMUM_SECTION_SIZE) {
            throw new LineRemoveSectionException("상행 종점역과 하행 종점역만 있습니다");
        }
    }

    public Station getFirstStation() {
        return getFirstSection().getUpStation();
    }

    private Section getFirstSection() {
        checkSizeNotEmpty();

        final var downStations = getDownStations();
        return getValue().stream()
                .filter(it -> !downStations.contains(it.getUpStation()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("노선의 상행 종점역을 찾을 수 없습니다"));
    }

    public Station getLastStation() {
        return getLastSection().getDownStation();
    }

    private Section getLastSection() {
        checkSizeNotEmpty();

        final var upStations = getUpStations();
        return getValue().stream()
                .filter(it -> !upStations.contains(it.getDownStation()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("노선의 하행 종점역을 찾을 수 없습니다"));
    }

    private void checkSizeNotEmpty() {
        if (this.value.isEmpty()) {
            throw new LineSectionsEmptyException();
        }
    }

    private List<Station> getUpStations() {
        return getValue().stream()
                .map(Section::getUpStation)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<Station> getDownStations() {
        return getValue().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Station> getStations() {
        final var upStation = getFirstStation();
        final var downStations = value.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toUnmodifiableList());

        final var stations = new ArrayList<Station>();
        stations.add(upStation);
        stations.addAll(downStations);
        return stations.stream()
                .collect(Collectors.toUnmodifiableList());
    }
}
