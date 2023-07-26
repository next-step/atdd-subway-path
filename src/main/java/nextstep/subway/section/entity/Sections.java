package nextstep.subway.section.entity;

import lombok.Getter;
import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.station.entity.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public Sections(Section section, Line line) {
        this.sections.add(section);
        section.addSection(line);
    }

    public void addSection(Section section, Line line){
        // 이미 등록된 구간인지 확인
        if (alreadySection(section)) {
            throw new SubwayException(ErrorCode.ALREADY_SECTION);
        }

        // 등록하려는 구간이 기존 구간에 포함되는지 확인
        if (!cannotAddSection(section)) {
            throw new SubwayException(ErrorCode.CAN_NOT_BE_ADDED_SECTION);
        }

        // 역 사이에 새로운 역을 등록할 경우
        if (isUptoUp(section)) {
            addNewStationBetweenExistingStation(section, line);
            return;
        }

        // 새로운 역을 상행 종점으로 등록할 경우
        if (isUpToDown(section)) {
            addNewStationAsAnUpStation(section);
            return;
        }

        // 새로운 역을 하행 종점으로 등록할 경우
        if (isDownToUp(section)) {
            addNewStationAsAnDownStation(section);
        }
    }

    public Set<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();

        // 첫번째 구간 찾기
        Section firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());

        // 첫번째 구간 외 나머지 구간 찾기
        Section nextSection = firstSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = getNextSection(nextSection);
        }

        return stations;
    }

    public void addNewStationAsAnDownStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> this.sections.add(section));
    }

    public void addNewStationAsAnUpStation(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> this.sections.add(section));
    }

    public void addNewStationBetweenExistingStation(Section section, Line line) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    this.sections.add(Section.of(oldSection.getUpStation(), section.getDownStation(), validationDistance(oldSection.getDistance(), section.getDistance()), line));
                    this.sections.add(Section.of(section.getDownStation(), oldSection.getDownStation(), section.getDistance(), line));
                    this.sections.remove(oldSection);
                });
    }

    public boolean isUptoUp(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

    public boolean isUpToDown(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getDownStation()));
    }

    public boolean isDownToUp(Section newSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().equals(newSection.getUpStation()));
    }

    public boolean alreadySection(Section section) {
        return sections.stream()
                .anyMatch(oldSection -> oldSection.getUpStation().equals(section.getUpStation()))
                &&
                sections.stream()
                        .anyMatch(oldSection -> oldSection.getDownStation().equals(section.getDownStation()));
    }

    public boolean cannotAddSection(Section newSection) {
        return sections.stream()
                .anyMatch(oldSection -> {
                    boolean b1 = oldSection.getUpStation().equals(newSection.getUpStation());
                    boolean b2 = oldSection.getUpStation().equals(newSection.getDownStation());
                    return b1 || b2;
                }) ||
                sections.stream()
                        .anyMatch(oldSection -> {
                            boolean b1 = oldSection.getDownStation().equals(newSection.getUpStation());
                            boolean b2 = oldSection.getDownStation().equals(newSection.getDownStation());
                            return b1 || b2;
                        });
    }

    private int validationDistance(int oldDistance, int newDistance) {
        int distance = oldDistance - newDistance;
        if (distance <= 0) {
            throw new SubwayException(ErrorCode.INVALID_DISTANCE);
        }
        return distance;
    }

    public void removeSection(Station removeStation, Line line) {
        if (isSectionOne()) {
            throw new SubwayException(ErrorCode.SECTION_IS_ONE);
        }

        newSections(removeStation, line);

        this.sections.removeIf(section -> section.isExistsStation(removeStation));
        line.modifyDistance();
    }

    /**
     * <pre>
     * 제거하려는 역이 첫번째 역, 마지막 역이 아닌 경우
     * 첫번째 구간의 상행역을 새로운 구간의 상행역으로 저장
     * 그 다음 구간의을 첫번째 구간의 하행역으로 저장 -> 반복
     * </pre>
     */
    private void newSections(Station removeStation, Line line) {
        // 첫번째 역 찾기
        Section firstSection = getFirstSection();

        if (isNotFirstAndLastStation(firstSection, removeStation)) {
            Section nextSection = getNextSection(firstSection);
            while (nextSection != null) {
                Station upStation = firstSection.getUpStation();
                Station downStation = getNewDownStation(removeStation, nextSection);
                int distance = firstSection.getDistance() + nextSection.getDistance();
                this.sections.add(Section.of(upStation, downStation, distance, line));
                firstSection = nextSection;
                nextSection = getNextSection(nextSection);
            }
        }
    }

    public Section getFirstSection() {
        List<Station> downStations = this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return this.sections.stream()
                .filter(it -> !downStations.contains(it.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new SubwayException(ErrorCode.INVALID_UP_STATION));
    }

    public Section getNextSection(Section section) {
        return this.sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    private boolean isSectionOne() {
        return this.sections.size() == 1;
    }

    private boolean isLastSection(Station downStation) {
        return getLastSection()
                .map(section -> section.isExistsStation(downStation))
                .orElse(false);
    }

    private boolean isNotFirstAndLastStation(Section firstSection, Station removeStation) {
        return firstSection.getDownStation().equals(removeStation) || getLastSection()
                .map(section -> !section.getDownStation().equals(removeStation))
                .orElseGet(() -> false);
    }

    private Station getNewDownStation(Station removeStation, Section nextSection) {
        if (isNextUpStationEqualsRemoveStation(nextSection, removeStation)) {
            return nextSection.getDownStation();
        } else {
            return  nextSection.getUpStation();
        }
    }

    private boolean isNextUpStationEqualsRemoveStation(Section nextSection, Station removeStation) {
        return nextSection.getUpStation().equals(removeStation);
    }

    private Optional<Section> getLastSection() {
        if (sections.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(sections.get(sections.size() - 1));
        }
    }
}
