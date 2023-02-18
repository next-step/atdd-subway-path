package nextstep.subway.domain.section;

import nextstep.subway.common.ErrorMessage;
import nextstep.subway.domain.Station;
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

    private final int REMOVE_MIN_SIZE = 2;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Station getLastStation() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getDownStation)
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
    }

    public Section get(int index) {
        return sections.get(index);
    }
    public List<Section> getSections() {
        return this.sections;
    }



    public void addSection(Section section) {
        addValidation(section);
        AddSectionFactory addSectionFactory = new AddSectionFactory();
        AddSectionStrategy addSectionStrategy = addSectionFactory.createAddSection(this, section);
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

    public Station getFirstStation() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .map(Section::getUpStation)
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
    }

    void addSectionElement(Section section) {
        this.sections.add(section);
    }



    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station nowStation = getFirstStation();
        Station lastStation = getLastStation();
        stations.add(nowStation);

        while (!Objects.equals(nowStation, lastStation)) {
            Section section = getUpSection(nowStation).orElseThrow(() -> new IllegalStateException(ErrorMessage.INVALID_SECTION_STATE.toString()));
            nowStation = section.getDownStation();
            stations.add(nowStation);
        }
        return stations;
    }

    public Optional<Section> getUpSection(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst();
    }

    public Optional<Section> getDownSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(downStation))
                .findFirst();
    }

    public void removeSectionCollection(Station station) {
        Optional<Section> downSection = getDownSection(station);
        Optional<Section> upSection = getUpSection(station);
        removeValidation(station, upSection, downSection);

        RemoveSectionFactory removeSectionFactory = new RemoveSectionFactory();
        RemoveSectionStrategy removeSectionStrategy = removeSectionFactory.createRemoveSectionStrategy(this, station);
        removeSectionStrategy.removeSection(this, station);
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
    }

    private void removeValidation(Station station, Optional<Section> upSection, Optional<Section> downSection) {
        if (ObjectUtils.isEmpty(sections) || sections.size() < REMOVE_MIN_SIZE) {
            throw new IllegalStateException(ErrorMessage.ENOUGH_NOT_SECTION_SIZE.toString());
        }
        if (upSection.isEmpty() && downSection.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.NOT_CONNECT_STATION.toString());
        }
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
