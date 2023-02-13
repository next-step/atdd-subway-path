package nextstep.subway.domain;

import nextstep.subway.common.ErrorMessage;
import org.springframework.util.ObjectUtils;

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
        addValidation(section);
        AddSectionStrategy addSectionStrategy = createAddSection(section);
        addSectionStrategy.addSection(this, section);
    }

    private void addValidation(Section section) {
        if (ObjectUtils.isEmpty(sections)) {
            return;
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        List<Station> stations = getStations();
        duplicatedValid(upStation, downStation, stations);
        connectedValid(upStation, downStation, stations);
    }

    private static void connectedValid(Station upStation, Station downStation, List<Station> stations) {
        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new IllegalStateException(ErrorMessage.NOT_CONNECT_STATION.toString());
        }
    }

    private static void duplicatedValid(Station upStation, Station downStation, List<Station> stations) {
        if (stations.contains(upStation) && stations.contains(downStation)) {
            throw new IllegalStateException(ErrorMessage.DUPLICATED_STATION.toString());
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

    public void removeSection(Station station) {
        List<Section> sections = getSections();
        int index = sections.size() - 1;

        if (index < 1) {
            throw new IllegalStateException(ErrorMessage.ENOUGH_NOT_SECTION_SIZE.toString());
        }
        if (!sections.get(index).getDownStation().equals(station)) {
            throw new IllegalArgumentException(ErrorMessage.ENOUGH_REMOVE_DOWN.toString());
        }

        sections.remove(index);
    }

    interface AddSectionStrategy {
        void addSection(SectionCollection sectionCollection, Section section);
    }

    static class MiddleAddSection implements AddSectionStrategy {

        @Override
        public void addSection(SectionCollection sectionCollection, Section section) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            int distance = section.getDistance();
            sectionCollection.getUpStation(upStation).ifPresent(updateSection -> updateSection.updateUpStation(downStation, distance));
            sectionCollection.getDownStation(downStation).ifPresent(updateSection -> updateSection.updateDownStation(upStation, distance));
            sectionCollection.getSections().add(section);
        }
    }

    static class BasicAddSection implements AddSectionStrategy {

        @Override
        public void addSection(SectionCollection sectionCollection, Section section) {
            sectionCollection.getSections().add(section);
        }
    }
}
