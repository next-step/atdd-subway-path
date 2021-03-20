package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private final static int MINIMUM_SECTION_SIZE = 1 ;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        /*
        return this.sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
         */
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
        if (sections.size() <= MINIMUM_SECTION_SIZE) {
            throw  new ApplicationException(ApplicationType.LINE_MUST_BE_HAVE_ONE_SECTION_AT_LEAST);
        }

        if (!isLastDownStationContains(station)) {
            throw new ApplicationException(ApplicationType.ONLY_DOWN_STATIONS_CAN_BE_DELETED);
        }

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

        for (int targetIndex=0; targetIndex< size; targetIndex++) {
            Section currentSection = this.sections.get(targetIndex);

            if (currentSection.isAddableInMiddle(addSection)) {

                Section frontSection = new Section(currentSection.getLine(), addSection.getUpStation(), addSection.getDownStation(), addSection.getDistance());
                Section afterSection = new Section(currentSection.getLine(), addSection.getDownStation(), currentSection.getDownStation(), currentSection.getDistance() - addSection.getDistance());

                this.sections.set(targetIndex, afterSection);
                this.sections.add(targetIndex, frontSection);
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

    public int getTotalDistance() {
        return getSections().stream().mapToInt(sec -> sec.getDistance()).sum();
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {

        if (getStations().size() == 0) {
            addSectionInLast(line, upStation, downStation, distance);
            return;
        }

        //둘다 등록된 역 혹은 둘다 등록되지 않은역은 등록불가
        validateSectionAddable(upStation, downStation, distance);

        if (isFirstStationEqualsWithDownStation(downStation)) {
            addSectionInFirst(line, upStation, downStation, distance);
            return;
        }

        if (isLastDownStationContains(upStation)) {
            addSectionInLast(line, upStation, downStation, distance);
            return;
        }


        if (isContainsStationInUpStation(upStation)) {
            addSectionInMiddle(line, upStation, downStation, distance);
            return;
        }
    }

    private boolean isFirstStationEqualsWithDownStation(Station station) {
        return station.equals(getFirstStation());
    }

    private void addSectionInFirst(Line line, Station upStation, Station downStation, int distance) {
        addSectionInFirst(new Section(line, upStation, downStation, distance));
    }

    private void addSectionInLast(Line line, Station upStation, Station downStation, int distance) {
        add(new Section(line, upStation, downStation, distance));
    }

    private void addSectionInMiddle(Line line, Station upStation, Station downStation, int distance) {

        addInMiddle(new Section(line, upStation, downStation, distance));
    }

    private boolean isContainsStationInUpStation(Station station) {
        if (getSectionUpStationContains(station) == null) {
            return false;
        }

        return true;
    }

    private void validateSectionAddable(Station upStation, Station downStation, int distance) {
        validateContainsBothStations(upStation, downStation);
    }

    private void validateContainsBothStations(Station upStation, Station downStation) {
        if(containsBothStation(upStation, downStation)) {
            throw new ApplicationException(ApplicationType.STATIONS_ALREADY_REGISTERD);
        }

        if(notContainsBothStation(upStation, downStation)) {
            throw new ApplicationException(ApplicationType.ONE_STATION_MUST_BE_REGISTERED_AT_LEAST);
        }
    }

    public List<Station> getSortedStations() {
        return sections.stream()
                .sorted()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public int getSize() {
        return this.sections.size();
    }
}
