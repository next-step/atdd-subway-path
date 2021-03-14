package nextstep.subway.line.domain;

import nextstep.subway.line.exception.CannotRemoveSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.CannotMatchingStationException;
import nextstep.subway.station.exception.CannotRemoveStationException;
import nextstep.subway.station.exception.StationAlreadyExistException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.exception.LineExceptionMessage.EXCEPTION_MESSAGE_NOT_DELETABLE_SECTION;
import static nextstep.subway.station.exception.StationExceptionMessage.*;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            sections.add(createSection(line, upStation, downStation, distance));
            return;
        }

        validateUpStationMatching(upStation);
        validateExistDownStation(downStation);

        sections.add(createSection(line, upStation, downStation, distance));
    }

    private Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    private void validateUpStationMatching(Station upStation) {
        if (!getLastDownStation().equals(upStation)) {
            throw new CannotMatchingStationException(EXCEPTION_MESSAGE_NOT_MATCHING_EXISTING_AND_NEW_STATION);
        }
    }

    private void validateExistDownStation(Station downStation) {
        if (getLastDownStation().equals(downStation)) {
            throw new StationAlreadyExistException(EXCEPTION_MESSAGE_EXIST_DOWN_STATION);
        }
    }

    public void deleteLastDownStation(Long downStationId) {
        Station lastDownStation = getLastDownStation();
        validateDeletableStation(downStationId, lastDownStation.getId());

        sections.remove(getLastSection());
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    private Station getLastDownStation() {
        Section section = getLastSection();
        return section.getDownStation();
    }

    private void validateDeletableStation(Long downStationId, Long lastDownStationId) {
        boolean isEqualStation = lastDownStationId.equals(downStationId);

        if (!isEqualStation) {
            throw new CannotRemoveStationException(EXCEPTION_MESSAGE_NOT_DELETABLE_STATION);
        }
        if (sections.size() == 1) {
            throw new CannotRemoveSectionException(EXCEPTION_MESSAGE_NOT_DELETABLE_SECTION);
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
