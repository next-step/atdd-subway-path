package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if (isSectionNotEmpty()) {
            stations.add(this.sections.get(0).getUpStation());

            this.sections.forEach(section -> {
                stations.add(section.getDownStation());
            });
        }

        return stations;
    }

    public void removeSection(Station station) {
        this.sections.stream()
                .filter(it -> it.getDownStation().getId() == station.getId())
                .findFirst()
                .ifPresent(it -> this.sections.remove(it));
    }

    private boolean isSectionNotEmpty() {
        return !this.sections.isEmpty();
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public void addSectionInFirst(Section section) {
        this.sections.add(0, section);
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public void addInMiddle(Section addSection) {

        int size = getSections().size();

        for (int i=0; i< size; i++) {
            Section currentSection = this.sections.get(i);
            if (currentSection.isAddableInMiddle(addSection)) {

                Section frontSection = new Section(currentSection.getLine(), addSection.getUpStation(), addSection.getDownStation(), addSection.getDistance());
                Section afterSection = new Section(currentSection.getLine(), addSection.getDownStation(), currentSection.getDownStation(), currentSection.getDistance() - addSection.getDistance());

                //현재 section 삭제
                this.sections.remove(i);
                //두개로 쪼갠다.
                this.sections.add(i, afterSection);
                this.sections.add(i, frontSection);
            }
        }
    }

    private boolean containsStation(Station station) {
        return getStations().stream().filter(s -> s.getId().equals(station.getId())).count() > 0;
    }

    public boolean containsBothStation(Station upStation, Station downStation) {
        return containsStation(upStation) && containsStation(downStation);
    }

    public boolean notContainsBothStation(Station upStation, Station downStation) {
        return !containsStation(upStation) && !containsStation(downStation);
    }

    public Section getSectionUpStationContains(Station upStation) {
        return this.sections.stream().filter(s -> s.getUpStation().getId().equals(upStation.getId())).findFirst().orElse(null);
    }

    public Station getLastStation() {
        int lastIndex = getStations().size()-1;

        return getStations().get(lastIndex);
    }

    public Station getFirstStation() {
        return this.getStations().stream().findFirst().orElseGet(null);
    }

    public boolean isLastDownStationContains(Station station) {
        if (getLastStation().getId().equals(station.getId())) {
            return true;
        }

        return false;
    }

    public int getToalDistance() {
        return getSections().stream().mapToInt(sec -> sec.getDistance()).sum();
    }
}
