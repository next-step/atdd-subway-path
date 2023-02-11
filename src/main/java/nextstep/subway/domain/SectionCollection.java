package nextstep.subway.domain;

import nextstep.subway.common.ErrorMessage;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class SectionCollection {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public List<Section> getSections() {
        return sections;
    }

    private Station getLastStation() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getDownStation)
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
    }

    public void addSection(Section section) {
        validation(section);
        AddSectionStrategy addSectionStrategy = createAddSection(section);
        addSectionStrategy.addSection(section);
    }

    private void validation(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        List<Station> stations = getStations();
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new IllegalStateException(ErrorMessage.DUPLICATED_STATION.toString());
        }
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new IllegalStateException(ErrorMessage.NOT_CONNECT_STATION.toString());
        }
    }

    private Station getFirstStation() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
    }


    AddSectionStrategy createAddSection(Section section) {
        if (sections.isEmpty()) {
            return new BasicAddSection();
        }
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        Station firstStation = getFirstStation();
        Station lastStation = getLastStation();

        if (Objects.equals(downStation, firstStation) || Objects.equals(upStation, lastStation)) {
            return new BasicAddSection();
        }

        return new MiddleAddSection();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station nowStation = getFirstStation();
        Station lastStation = getLastStation();
        stations.add(nowStation);

        while (!Objects.equals(nowStation, lastStation)) {
            Section section = getUpStation(nowStation).orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
            nowStation = section.getDownStation();
            stations.add(nowStation);
        }
        return stations;
    }

    private Optional<Section> getUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    private Optional<Section> getDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    interface AddSectionStrategy {
        void addSection(Section section);
    }

    class MiddleAddSection implements AddSectionStrategy {

        @Override
        public void addSection(Section section) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            int distance = section.getDistance();
            getUpStation(upStation).ifPresent(updateSection -> updateSection.updateUpStation(downStation, distance));
            getDownStation(downStation).ifPresent(updateSection -> updateSection.updateDownStation(upStation, distance));
            sections.add(section);
        }
    }

    class BasicAddSection implements AddSectionStrategy {

        @Override
        public void addSection(Section section) {
            sections.add(section);
        }
    }
}
