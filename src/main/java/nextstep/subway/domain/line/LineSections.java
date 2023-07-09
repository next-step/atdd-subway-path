package nextstep.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.line.exception.LineAppendSectionException;
import nextstep.subway.domain.line.exception.LineRemoveSectionException;
import nextstep.subway.domain.line.exception.LineSectionsEmptyException;
import nextstep.subway.domain.station.Station;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LineSections {
    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private final List<Section> value = new ArrayList<>();

    private static final int MINIMUM_SECTION_SIZE = 1;

    public static LineSections init(final Section section) {
        final var sections = new LineSections();
        sections.value.add(section);
        return sections;
    }

    public void append(final Section section) {
        requireSectionAppendable(section);
        value.add(section);
    }

    private void requireSectionAppendable(final Section section) {
        if (!getLastStation().equals(section.getUpStation())) {
            throw new LineAppendSectionException(String.format(
                    "노선의 하행역이 구간의 상행역과 일치하지 않습니다 : 노선의 하행역 id=%d, 구간의 상행역 id=%d",
                    getLastStation().getId(), section.getUpStation().getId()
            ));
        }
        if (getStations().contains(section.getDownStation())) {
            throw new LineAppendSectionException(
                    "구간의 하행역이 이미 노선에 포함되어 있습니다 : 구간의 하행역 id=" + section.getDownStation().getId()
            );
        }
    }

    public void remove(final Station station) {
        requireStationRemovable(station);
        value.remove(getLastSection());
    }

    private void requireStationRemovable(final Station station) {
        if (!getLastStation().equals(station)) {
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
        return value.get(0);
    }

    public Station getLastStation() {
        return getLastSection().getDownStation();
    }

    private Section getLastSection() {
        checkSizeNotEmpty();
        return value.get(value.size() - 1);
    }

    private void checkSizeNotEmpty() {
        if (this.value.isEmpty()) {
            throw new LineSectionsEmptyException();
        }
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
