package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final String NO_FIRST_STATION_MESSAGE = "상행 종점역이 존재하지 않습니다.";
    private static final String INVALID_DISTANCE_MESSAGE = "신규 역 사이의 길이가 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.";
    private static final int LAST_INDEX_VALUE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void addSection(Section section) {
        if (isAlreadyExistSection(section) || nonExistStation(section)) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
        
        if (isMiddleSection(section)) {
            addMiddleSection(section);
            return;
        }

        sectionList.add(section);
    }

    private boolean nonExistStation(Section section) {
        return !sectionList.isEmpty() && sectionList.stream()
                .noneMatch(otherSection ->
                        section.getUpStation().equals(otherSection.getUpStation())
                        || section.getUpStation().equals(otherSection.getDownStation())
                        || section.getDownStation().equals(otherSection.getUpStation())
                        || section.getDownStation().equals(otherSection.getDownStation())
                );
    }

    private boolean isAlreadyExistSection(Section section) {
        return sectionList.stream()
                .anyMatch(section::equals);
    }

    private boolean isMiddleSection(Section section) {
        return sectionList.stream()
                .anyMatch(otherSection ->
                        section.getUpStation().equals(otherSection.getUpStation())
                                || section.getDownStation().equals(otherSection.getDownStation()));
    }

    private void addMiddleSection(Section section) {
        for (Section otherSection : sectionList) {
            if (section.getUpStation().equals(otherSection.getUpStation())) {
                validateDistance(section, otherSection);
                otherSection.changeUpStation(section.getDownStation(), section.getDistance());
                break;
            }

            if (section.getDownStation().equals(otherSection.getDownStation())) {
                validateDistance(section, otherSection);
                otherSection.changeDownStation(section.getUpStation(), section.getDistance());
                break;
            }
        }
        sectionList.add(section);
    }

    private void validateDistance(Section section, Section otherSection) {
        if (section.getDistance() >= otherSection.getDistance()) {
            throw new IllegalArgumentException(INVALID_DISTANCE_MESSAGE);
        }
    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public List<Station> getStationList() {
        if (sectionList.isEmpty()) {
            return Collections.emptyList();
        }

        Section firstSection = findFirstSection();
        List<Station> stations = makeStationList(firstSection);

        return Collections.unmodifiableList(stations);
    }

    private List<Station> makeStationList(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        while (true) {
            Section finalSection = section;
            Optional<Section> nextSection = sectionList.stream()
                    .filter(otherSection -> finalSection.getDownStation().equals(otherSection.getUpStation()))
                    .findAny();

            if (nextSection.isPresent()) {
                stations.add(nextSection.get().getUpStation());
                section = nextSection.get();
                continue;
            }

            break;
        }

        stations.add(section.getDownStation());
        return stations;
    }

    private Section findFirstSection() {
        for (Section section : sectionList) {
            if (sectionList.stream()
                    .noneMatch(otherSection -> section.getUpStation().equals(otherSection.getDownStation()))) {
                return section;
            }
        }
        throw new IllegalArgumentException(NO_FIRST_STATION_MESSAGE);
    }

    public void deleteSection(Station station) {
        if (!lastStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sectionList.remove(lastIndex());
    }

    private Station lastStation() {
        return sectionList.get(lastIndex()).getDownStation();
    }

    private int lastIndex() {
        return sectionList.size() - LAST_INDEX_VALUE;
    }
}
