package nextstep.subway.line.domain;

import jdk.nashorn.internal.runtime.options.Option;
import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {

    private final static int MINIMUM_SECTION_SIZE = 1 ;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public void removeSection(Station station) {
        if (sections.size() <= MINIMUM_SECTION_SIZE) {
            throw  new ApplicationException(ApplicationType.LINE_MUST_BE_HAVE_ONE_SECTION_AT_LEAST);
        }

        //마지막역 삭제시
        if (station.equals(getLastStation())) {
            this.sections.stream()
                    .filter(it -> it.getDownStation().getId() == station.getId())
                    .findFirst()
                    .ifPresent(it -> this.sections.remove(it));

            return;
        }

        for (int i=0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (currentSection.getDownStation().equals(station)) {
                sections.get(i).extendSection(sections.get(i+1).getDownStation(), sections.get(i+1).getDistance());
                sections.remove(i+1);
            }
        }

        //중간역 삭제시
        this.sections.stream()
                .filter(it -> it.getDownStation().getId() == station.getId())
                .findFirst()
                .ifPresent(it -> this.sections.remove(it));
    }

    public Optional<Section> containsDownStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.getDownStation().equals(station))
                .findFirst();
    }

    public List<Section> getSections() {
        return this.sections;
    }

    private void prepend(Section section) {
        this.sections.add(0, section);
    }

    private void append(Section section) {
        this.sections.add(section);
    }

    private void addInMiddle(Section addSection) {
        int size = getSections().size();

        for (int targetIndex=0; targetIndex< size; targetIndex++) {
            Section currentSection = this.sections.get(targetIndex);

            if (currentSection.isSameUpStation(addSection)) {

                validateDistance(currentSection, addSection);

                Section frontSection = new Section(currentSection.getLine(), addSection.getUpStation(), addSection.getDownStation(), addSection.getDistance());
                Section afterSection = new Section(currentSection.getLine(), addSection.getDownStation(), currentSection.getDownStation(), currentSection.getDistance() - addSection.getDistance());

                this.sections.set(targetIndex, afterSection);
                this.sections.add(targetIndex, frontSection);
            }
        }
    }

    private void validateDistance(Section currentSection, Section addSection) {
        if (!currentSection.addable(addSection)) {
            throw new ApplicationException(ApplicationType.SECTION_DISTANCE_CANNOT_BE_LONGER_THAN_CURRENT_SECTION);
        }
    }

    private Section getSectionsUpStationContains(Station upStation) {
        return this.sections.stream().filter(s -> s.getUpStation().equals(upStation)).findFirst().orElse(null);
    }

    private Station getLastStation() {
        int lastIndex = getSortedStations().size()-1;
        return getSortedStations().get(lastIndex);
    }

    private Station getFirstStation() {
        return this.getSortedStations().stream().findFirst().orElseGet(null);
    }

    public int getTotalDistance() {
        return getSections().stream().mapToInt(sec -> sec.getDistance()).sum();
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (getSortedStations().size() == 0) {
            append(new Section(line, upStation, downStation, distance));
            return;
        }

        //둘다 등록된 역 혹은 둘다 등록되지 않은역은 등록불가
        validateContainsBothStations(upStation, downStation);

        if (downStation.equals(getFirstStation())) {
            prepend(new Section(line, upStation, downStation, distance));
            return;
        }

        if (upStation.equals(getLastStation())) {
            append(new Section(line, upStation, downStation, distance));
            return;
        }

        if (hasSameUpStation(upStation)) {
            addInMiddle(new Section(line, upStation, downStation, distance));
            return;
        }
    }

    private boolean hasSameUpStation(Station station) {
        if (getSectionsUpStationContains(station) == null) {
            return false;
        }

        return true;
    }

    private boolean containsStation(Station station) {
        return getSortedStations().stream().filter(s -> s.getId().equals(station.getId())).count() > 0;
    }

    private void validateContainsBothStations(Station upStation, Station downStation) {
        if(containsStation(upStation) && containsStation(downStation)) {
            throw new ApplicationException(ApplicationType.STATIONS_ALREADY_REGISTERD);
        }

        if(!containsStation(upStation) && !containsStation(downStation)) {
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
}
