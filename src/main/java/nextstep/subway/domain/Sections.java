package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Embeddable
public class Sections {
    private static final int MIN_SIZE = 1;
    private static final int INDEX_OF_END_SECTION = -1;
    private static final String ALREADY_REGISTERED_SECTION = "이미 등록된 구간입니다.";
    private static final String OVER_DISTANCE_SECTION = "길이가 더 긴 구간은 추가할 수 없습니다.";
    private static final String UNABLE_TO_REMOVE_LAST_SECTION = "구간이 1개일 경우 삭제가 불가능합니다.";
    private static final String UNABLE_TO_REMOVE_NON_LAST_STATION = "마지막 역만 삭제가 가능합니다.";
    private static final String UNREGISTERED_STATIONS = "요청한 상/하행역이 모두 노선에 등록되지 않았습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        List<Station> stations = getStations();

        boolean hasUpStation = stations.contains(upStation);
        boolean hasDownStation = stations.contains(downStation);

        validateAddable(hasUpStation, hasDownStation);
        addBetweenSection(section, hasUpStation, hasDownStation);

        sections.add(section);
    }

    public void removeStation(Station station) {
        List<Station> stations = getStations();

        validateRemovable(station, stations);

        if (isFirstStation(station)) {
            removeFirstSection(station);
            return;
        }
        if (isLastStation(station)) {
            removeLastSection(station);
            return;
        }
        removeMiddleSection(station);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Section> sorted = sort();
        List<Station> stations = sorted.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(sorted.get(sorted.size() - 1).getDownStation());

        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    private boolean isFirstStation(Station station) {
        return findFirstStation().equals(station);
    }

    private boolean isLastStation(Station station) {
        return findLastStation().equals(station);
    }

    private void removeFirstSection(Station station) {
        int index = indexOfSectionByUpStation(station);
        sections.remove(index);
    }

    private void removeLastSection(Station station) {
        int index = indexOfSectionByDownStation(station);
        sections.remove(index);
    }

    private void removeMiddleSection(Station station) {
        int indexOfRemoveSection = indexOfSectionByDownStation(station);
        int indexOfNextSection = indexOfSectionByUpStation(station);

        Section removeSection = sections.get(indexOfRemoveSection);
        Section nextSection = sections.get(indexOfNextSection);


        Station newUpStation = removeSection.getUpStation();
        Station newDownStation = nextSection.getDownStation();
        int totalDistance = removeSection.getDistance() + nextSection.getDistance();

        nextSection.updateSection(newUpStation, newDownStation, totalDistance);

        sections.remove(indexOfRemoveSection);
    }

    private void validateRemovable(Station station, List<Station> stations) {
        if (!stations.contains(station)) {
            throw new IllegalArgumentException("등록되지 않은 역입니다.");
        }

        if (sections.size() == MIN_SIZE) {
            throw new IllegalArgumentException(UNABLE_TO_REMOVE_LAST_SECTION);
        }
    }

    private void addBetweenSection(Section section, boolean hasUpStation, boolean hasDownStation) {
        if (hasUpStation) {
            int index = indexOfSectionByUpStation(section.getUpStation());

            addBetweenSectionsByUpStation(index, section);
        }

        if (hasDownStation) {
            int index = indexOfSectionByDownStation(section.getDownStation());

            addBetweenSectionsByDownStation(index, section);
        }
    }

    private void validateAddable(boolean hasUpStation, boolean hasDownStation) {
        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException(ALREADY_REGISTERED_SECTION);
        }

        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException(UNREGISTERED_STATIONS);
        }
    }

    private void addBetweenSectionsByDownStation(int index, Section section) {
        if (index == INDEX_OF_END_SECTION) {
            return;
        }

        Station upStation = section.getUpStation();
        int newDistance = section.getDistance();

        Section oldSection = sections.get(index);
        if (oldSection.getDistance() <= newDistance) {
            throw new IllegalArgumentException(OVER_DISTANCE_SECTION);
        }
        oldSection.updateSection(oldSection.getUpStation(), upStation, oldSection.getDistance() - newDistance);
    }

    private void addBetweenSectionsByUpStation(int index, Section section) {
        if (index == INDEX_OF_END_SECTION) {
            return;
        }

        Station downStation = section.getDownStation();
        int newDistance = section.getDistance();

        Section oldSection = sections.get(index);
        if (oldSection.getDistance() <= newDistance) {
            throw new IllegalArgumentException(OVER_DISTANCE_SECTION);
        }
        oldSection.updateSection(downStation, oldSection.getDownStation(), oldSection.getDistance() - newDistance);

    }

    private int indexOfSectionByUpStation(Station station) {
        return indexOfSectionByStation(station, x -> sections.get(x).getUpStation());
    }

    private int indexOfSectionByDownStation(Station station) {
        return indexOfSectionByStation(station, x -> sections.get(x).getDownStation());
    }

    private int indexOfSectionByStation(Station station, IntFunction<Station> func) {
        return IntStream.range(0, sections.size())
                .filter(index -> func.apply(index).equals(station))
                .findFirst()
                .orElse(INDEX_OF_END_SECTION);
    }

    private List<Section> sort() {
        List<Section> sorted = new ArrayList<>();
        Station first = findFirstStation();
        Station last = findLastStation();

        Station now = first;

        do {
            Section section = findOneByUpStation(now);
            sorted.add(section);
            now = section.getDownStation();
        } while (!now.equals(last));

        return sorted;
    }

    private Section findOneByUpStation(Station station) {
        return sections.stream()
                .filter(section -> station.equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Station findFirstStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Station findLastStation() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

}
