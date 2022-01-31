package nextstep.subway.domain;

import nextstep.subway.exception.IllegalSectionArgumentException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    public static final String NOT_LAST_SECTION = "마지막 구간이 아닙니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Line line, Section newSection) {
        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        boolean isNext = isNextSection(newSection.getUpStation());
        boolean isPrevious = isPreviousSection(newSection.getDownStation());

        if (!isNext && !isPrevious) {
            throw new IllegalSectionArgumentException("하나라도 지하철이 포함되어 있어야 합니다.");
        }

        if (isNext && isPrevious) {
            throw new IllegalSectionArgumentException("상행과 하행이 모두 일치할수 없습니다.");
        }

        if (isNext) {
            Section section1 = this.sections.stream()
                    .filter(section -> section.isUpStation(newSection.getUpStation()))
                    .findFirst()
                    .orElse(null);

            if (section1 == null) {
                this.sections.add(newSection);
                return;
            }

            int index = this.sections.indexOf(section1);
            this.sections.set(index, newSection);
            this.sections.add(index + 1,
                    new Section(
                            line,
                            newSection.getDownStation(),
                            section1.getDownStation(),
                            section1.getDistance() - newSection.getDistance()));
        }

        if (isPrevious) {
            Section section1 = this.sections.stream()
                    .filter(section -> section.isDownStation(newSection.getDownStation()))
                    .findFirst()
                    .orElse(null);

            if (section1 == null) {
                this.sections.add(0, newSection);
                return;
            }

            int index = this.sections.indexOf(section1);
            this.sections.set(index,
                    new Section(
                            line,
                            section1.getUpStation(),
                            newSection.getUpStation(),
                            section1.getDistance() - newSection.getDistance()));
            this.sections.add(index + 1, newSection);
        }

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

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        Section section = this.sections.get(0);
        section = getFirstSection(section);

        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());

        while (true) {
            Station downStation = section.getDownStation();
            System.out.println("downStation = " + downStation.getName());
            section = getNextSection(section.getDownStation());
            if (section == null) {
                break;
            }
            System.out.println("stations = " + section.getDownStation().getName());
            stations.add(section.getDownStation());
        }

        return stations;
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

    public void remove(Station station) {
        Section lastSection = getLastSection();
        if (!lastSection.isLast(station)) {
            throw new IllegalSectionArgumentException(NOT_LAST_SECTION);
        }

        this.sections.remove(lastSection);
    }

    private Section getLastSection() {
        return this.sections.get(this.sections.size() - 1);
    }

}
