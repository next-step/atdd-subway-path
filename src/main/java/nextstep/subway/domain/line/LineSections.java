package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private static final LineSectionAppender LINE_SECTION_APPENDER = new LineSectionAppender();
    private static final LineSectionRemover LINE_SECTION_REMOVER = new LineSectionRemover();

    public static LineSections init(final Section section) {
        final var sections = new LineSections();
        sections.value.add(section);
        return sections;
    }

    public void append(final Section section) {
        LINE_SECTION_APPENDER.append(this, section);
    }

    public void remove(final Station station) {
        LINE_SECTION_REMOVER.remove(this, station);
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

    public Optional<Section> findSectionContainsAsDownStation(final Station station) {
        return value.stream()
                .filter(section -> section.equalsDownStation(station))
                .findAny();
    }

    public Optional<Section> findSectionContainsAsUpStation(final Station station) {
        return value.stream()
                .filter(section -> section.equalsUpStation(station))
                .findAny();
    }

    public List<Station> getStations() {
        return value.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }
}
