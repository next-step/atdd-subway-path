package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionArgumentException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    public static final String NOT_EXISTS_STATION = "지하철역이 존재하지 않습니다.";
    public static final String WRONG_DISTANCE = "추가되는 구간의 거리가 기존의 거리보다 크거나 같을 수 없습니다.";
    public static final String REQUIRED_STATION = "하나라도 지하철이 포함되어 있어야 합니다.";
    public static final String UP_AND_DOWN_STATION_BOTH_CANNOT_EXISTS = "상행과 하행이 모두 존재할수 없습니다.";
    public static final String CAN_NOT_DELETE = "구간이 하나밖에 없는 경우 지하철역을 삭제할 수 없습니다.";
    public static final int MINIMUM_DISTANCE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Line line, Station upStation, Station downStation, int distance) {
        Section newSection = new Section(line, upStation, downStation, distance);

        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        boolean isNext = isNextSection(newSection.getUpStation());
        boolean isPrevious = isPreviousSection(newSection.getDownStation());

        validateStation(isNext, isPrevious);

        if (isNext) {
            addNextSection(line, newSection);
        }

        if (isPrevious) {
            addPreviousSection(line, newSection);
        }

    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Section section = getFirstSection(this.sections.get(0));

        return getSortStations(section);
    }

    public void remove(Line line, Station station) {
        if (!editable()) {
            throw new IllegalSectionArgumentException(CAN_NOT_DELETE);
        }

        Section previousSection = this.sections.stream()
                .filter(section1 -> section1.isDownStation(station))
                .findFirst()
                .orElse(null);

        Section nextSection = this.sections.stream()
                .filter(section1 -> section1.isUpStation(station))
                .findFirst()
                .orElse(null);

        if (previousSection == null && nextSection == null) {
            throw new IllegalSectionArgumentException(NOT_EXISTS_STATION);
        }

        if (previousSection == null) {
            this.sections.remove(nextSection);
            return;
        }

        if (nextSection == null) {
            this.sections.remove(previousSection);
            return;
        }

        int i = this.sections.indexOf(previousSection);
        this.sections.set(i, new Section(line,
                    previousSection.getUpStation(),
                    nextSection.getDownStation(),
                previousSection.getDistance() + nextSection.getDistance()));
        this.sections.remove(nextSection);
    }

    public boolean editable() {
        return this.sections.size() != 1;
    }

    private void validateStation(boolean isNext, boolean isPrevious) {
        if (!isNext && !isPrevious) {
            throw new IllegalSectionArgumentException(REQUIRED_STATION);
        }

        if (isNext && isPrevious) {
            throw new IllegalSectionArgumentException(UP_AND_DOWN_STATION_BOTH_CANNOT_EXISTS);
        }
    }

    private void addNextSection(Line line, Section newSection) {
        Section section = getNextSection(newSection.getUpStation());

        if (section == null) {
            this.sections.add(newSection);
            return;
        }

        validateDistance(newSection, section);
        addNextSectionByUpStation(line, newSection, section);
    }

    private void addNextSectionByUpStation(Line line, Section newSection, Section section) {
        int index = this.sections.indexOf(section);
        this.sections.set(index, newSection);
        this.sections.add(index + 1,
                new Section(
                        line,
                        newSection.getDownStation(),
                        section.getDownStation(),
                        section.getDistance() - newSection.getDistance()));
    }

    private void addPreviousSection(Line line, Section newSection) {
        Section section = getPreviousSection(newSection.getDownStation());

        if (section == null) {
            this.sections.add(0, newSection);
            return;
        }

        validateDistance(newSection, section);
        addPreviousSectionByDownStation(line, newSection, section);
    }

    private void validateDistance(Section newSection, Section section) {
        if ((section.getDistance() - newSection.getDistance()) < MINIMUM_DISTANCE) {
            throw new IllegalSectionArgumentException(WRONG_DISTANCE);
        }
    }

    private void addPreviousSectionByDownStation(Line line, Section newSection, Section section) {
        int index = this.sections.indexOf(section);
        this.sections.set(index,
                new Section(
                        line,
                        section.getUpStation(),
                        newSection.getUpStation(),
                        section.getDistance() - newSection.getDistance()));
        this.sections.add(index + 1, newSection);
    }

    private List<Station> getSortStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        while (true) {
            section = getNextSection(section.getDownStation());
            if (section == null) {
                break;
            }
            stations.add(section.getDownStation());
        }

        return stations;
    }

    private boolean isNextSection(Station station) {
        return this.sections.stream()
                .anyMatch(section ->
                        section.isUpStation(station) ||
                                section.isDownStation(station));
    }

    private boolean isPreviousSection(Station station) {
        return this.sections.stream()
                .anyMatch(section ->
                        section.isUpStation(station) ||
                                section.isDownStation(station));
    }

    private Section getNextSection(Station station) {
        return this.sections.stream()
                .filter(s -> s.isUpStation(station))
                .findFirst().orElse(null);
    }

    private Section getPreviousSection(Station station) {
        return this.sections.stream()
                .filter(s -> s.isDownStation(station))
                .findFirst().orElse(null);
    }

    private Section getFirstSection(Section section) {
        while (true) {
            Section previousSection = getPreviousSection(section.getUpStation());
            if (previousSection == null) {
                break;
            }

            section = previousSection;
        }
        return section;
    }

}
