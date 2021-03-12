package nextstep.subway.line.domain;

import nextstep.subway.exceptions.InvalidSectionDistanceException;
import nextstep.subway.exceptions.InvalidSectionException;
import nextstep.subway.exceptions.OnlyOneSectionException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Embeddable
public class Sections {
    public static final String ONLY_DOWN_STATION_CAN_DELETED = "하행 종점역만 삭제가 가능합니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sectionList = new ArrayList<>();

    public Sections() {
    }

    public Sections(Line line, List<Section> sections) {
        sections.forEach(section -> this.addSection(line, section));
    }

    public static Sections of(Line line, Section... sections) {
        return new Sections(line, Arrays.asList(sections));
    }

    public void addSection(Line line, Section section) {
        if (isEmpty()) {
            sectionList.add(section);
            section.updateLine(line);
            return;
        }
        //신규 구간이 역사이에 포함되는지 판단한다.
        boolean existsUpStation = existsByStation(section.getUpStation());
        boolean existsDownStation = existsByStation(section.getDownStation());

        checkExistsValdation(existsUpStation, existsDownStation);

        if (existsUpStation) {
            prependSection(line, section);
        }

        if (existsDownStation) {
            appendSection(line, section);
        }
    }

    private void checkExistsValdation(boolean existsUpStation, boolean existsDownStation) {
        if (existsUpStation && existsDownStation) {
            throw new InvalidSectionException("상행역과 하행역이 이미 노선에 등록되어 있으면 추가할 수 없습니다.");
        }

        if (!existsUpStation && !existsDownStation) {
            throw new InvalidSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    private void appendSection(Line line, Section section) {
        Section foundSection = findSectionByDownStation(section.getDownStation());
        if (Objects.isNull(foundSection)) {
            sectionList.add(section);
            section.updateLine(line);
            return ;
        }

        checkDistanceValidation(section, foundSection);

        int index = sectionList.indexOf(foundSection);

        Section prevSection = new Section(line, foundSection.getUpStation(), section.getUpStation(), Math.abs(section.getDistance() - foundSection.getDistance()));
        section.updateLine(line);

        sectionList.set(index, section);
        sectionList.add(index, prevSection);

    }

    private void prependSection(Line line, Section section) {
        Section foundSection = findSectionByUpStation(section.getUpStation());
        if (Objects.isNull(foundSection)) {
            sectionList.add(section);
            section.updateLine(line);
            return ;
        }

        checkDistanceValidation(section, foundSection);

        int index = sectionList.indexOf(foundSection);

        Section nextSection = new Section(line, section.getDownStation(), foundSection.getDownStation(), Math.abs(section.getDistance() - foundSection.getDistance()));
        section.updateLine(line);

        sectionList.set(index, section);
        sectionList.add(index + 1, nextSection);
    }

    private void checkDistanceValidation(Section section, Section foundSection) {
        if (foundSection.getDistance() <= section.getDistance()) {
            throw new InvalidSectionDistanceException();
        }
    }

    private Section findSectionByPredicate(Predicate<Section> predicate) {
        return sectionList.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    private Section findSectionByUpStation(Station upStation) {
        return findSectionByPredicate(section -> section.getUpStation().equals(upStation));
    }

    private Section findSectionByDownStation(Station downStation) {
        return findSectionByPredicate(section -> section.getDownStation().equals(downStation));
    }

    private boolean existsByStation(Station station) {
        return sectionList.stream()
                .anyMatch(section -> isEqualDownAndUpStation(station, section));
    }

    private boolean isEqualDownAndUpStation(Station station, Section section) {
        return Objects.equals(section.getUpStation(), station) || Objects.equals(section.getDownStation(), station);
    }

    public boolean isEmpty() {
        return sectionList.isEmpty();
    }

    public List<Station> getStationsInOrder() {
        if (isEmpty()) {
            return Arrays.asList();
        }
        List<Station> stations = new ArrayList<>();
        Section firstSection = sectionList.stream()
                .filter(section -> Objects.isNull(findSectionByDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(RuntimeException::new);


        stations.add(firstSection.getUpStation());
        getStations(stations, firstSection.getDownStation());

        return stations;
    }

    private void getStations(List<Station> stations, Station downStation) {
        stations.add(downStation);
        Section section = findSectionByUpStation(downStation);
        if (Objects.nonNull(section)) {
            getStations(stations, section.getDownStation());
        }
    }


    public Section getLastSection() {
        return sectionList.get(sectionList.size() - 1);
    }

    public Station getDownStation() {
        return sectionList.get(sectionList.size() - 1).getDownStation();
    }

    public void deleteLastSection(Long stationId) {
        isValidDeleteSection(stationId);
        sectionList.remove(getLastSection());
    }

    private void isValidDeleteSection(Long stationId) {
        Section lastSection = getLastSection();
        boolean isEqualDownStationAndTarget = lastSection.getDownStation()
                .getId()
                .equals(stationId);

        if (!isEqualDownStationAndTarget) {
            throw new InvalidSectionException();
        }

        if (sectionList.size() == 1) {
            throw new OnlyOneSectionException();
        }
    }
}
