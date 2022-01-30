package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;
import nextstep.subway.exception.section.AlreadyRegisteredStationInLineException;
import nextstep.subway.exception.section.DeleteLastDownStationException;
import nextstep.subway.exception.section.DownStationNotMatchException;
import nextstep.subway.exception.section.MinimumSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private static final String SECTION_FIRST_ADD_ERROR_MESSAGE = "첫 구간 추가시에만 가능";
    private static final int MINIMUM_SIZE_SECTION = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addFirstSection(Section section) {
        if (!sections.isEmpty()) {
            throw new ApplicationException(SECTION_FIRST_ADD_ERROR_MESSAGE);
        }
        sections.add(section);
    }

    public void addSection(Section section) {
        validateDownStation(section.getUpStation());
        validateAlreadyRegisteredStation(section.getDownStation());
        sections.add(section);
    }

    public void deleteStation(Station deleteStation) {
        validateDeleteLastDownStation(deleteStation);
        validateMinimumSection();

        int lastIndex = sections.size() - 1;
        sections.remove(lastIndex);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream()
                .map(it -> it.getUpStation())
                .collect(Collectors.toList());
        stations.add(getLastDownStation());

        return stations;
    }

    private boolean isEmpty() {
        return sections.isEmpty();
    }

    private void validateDownStation(Station upStation) {
        if (!isAddableSection(upStation)) {
            throw new DownStationNotMatchException(upStation.getName());
        }
    }

    private void validateAlreadyRegisteredStation(Station dowStation) {
        if (getAllStations().contains(dowStation)) {
            throw new AlreadyRegisteredStationInLineException(dowStation.getName());
        }
    }

    private void validateDeleteLastDownStation(Station deleteStation) {
        if (isEmpty()) {
            return;
        }

        Station lastDownStation = getLastDownStation();
        if (!Objects.equals(lastDownStation, deleteStation)) {
            throw new DeleteLastDownStationException(deleteStation.getName());
        }
    }

    private void validateMinimumSection() {
        if (sections.size() <= MINIMUM_SIZE_SECTION) {
            throw new MinimumSectionException();
        }
    }

    private boolean isAddableSection(Station upStation) {
        if (isEmpty()) {
            return true;
        }

        Station lastDownStation = getLastDownStation();
        return lastDownStation.equals(upStation);
    }

    private Station getLastDownStation() {
        int lastIndex = sections.size() - 1;
        Section section = sections.get(lastIndex);
        return section.getDownStation();
    }

}
