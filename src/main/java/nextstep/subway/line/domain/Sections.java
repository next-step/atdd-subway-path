package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .sorted()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public int size() {
        return getSections().size();
    }

    public void addSection(Section section) {

        List<Station> stations = this.getStations();
        sameStationValidate(section);

        if (stations.isEmpty()) {
            this.sections.add(section);
            return;
        }

        upAndDownExistsValidate(stations, section);
        upAndDownNotExistsValidate(stations, section);

        if (firstStationMatch(section)) {
            this.sections.add(section);
            return;
        }

        if (findLastStation().equals(section.getUpStation())) {
            this.sections.add(section);
            return;
        }

        Section preSection = findSection(section.getUpStation(), section.getDownStation());
        List<Section> newSections = addProcess(preSection, section);
        this.sections.remove(preSection);
        this.sections.addAll(newSections);
    }

    private List<Section> addProcess(Section preSection, Section section) {
        int newDistance = section.getDistance().getDistance();
        Section newSection;

        int nextDistance = preSection.getDistance().distanceDivide(newDistance);

        if (preSection.getUpStation().equals(section.getUpStation())) {
            newSection = new Section(preSection.getLine(), section.getDownStation(), preSection.getDownStation(), new Distance(nextDistance));

            return addSectionList(newSection, section);
        }

        newSection = new Section(preSection.getLine(), preSection.getUpStation(), section.getUpStation(), new Distance(nextDistance));

        return addSectionList(section, newSection);
    }

    private List<Section> addSectionList(Section newSection, Section section) {
        List<Section> addSections = new ArrayList<>();
        addSections.add(section);
        addSections.add(newSection);
        return addSections;
    }

    private Section findSection(Station upStation, Station downStation) {
        Section upSection = getSections().stream().filter(it -> it.getUpStation().equals(upStation))
                .findFirst().orElse(null);
        Section downSection = getSections().stream().filter(it -> it.getDownStation().equals(downStation))
                .findFirst().orElse(null);

        if (upSection == null) {
            return downSection;
        }

        return upSection;
    }

    public void removeSection(Station station) {
        sizeValidate();
        removeLastStationValidate(station);
        sections.remove(sections.size() - 1);
    }

    private boolean firstStationMatch(Section section) {
        return section.getDownStation().equals(this.getStations().get(0));
    }

    private void upAndDownExistsValidate(List<Station> stations, Section section) {
        if (stations.contains(section.getUpStation()) && stations.contains(section.getDownStation())) {
            throw new RuntimeException("상행역과 하행역이 이미 존재합니다.");
        }
    }

    private void upAndDownNotExistsValidate(List<Station> stations, Section section) {
        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new RuntimeException("일치하는 역이 없습니다.");
        }
    }

    private void sameStationValidate(Section section) {
        if (section.getUpStation().equals(section.getDownStation())) {
            throw new RuntimeException("구간의 두 역이 동일합니다.");
        }
    }

    private void sizeValidate() {
        if (this.sections.size() <= 1) {
            throw new RuntimeException("해당 라인에 구간이 1개 밖에 없습니다.");
        }
    }

    private void removeLastStationValidate(Station station) {
        if (!findLastStation().equals(station)) {
            throw new RuntimeException("해당 구간의 마지막 역이 아닙니다.");
        }
    }

    private Station findLastStation() {
        Section lastSection = this.getSections().get(sections.size() - 1);
        return lastSection.getDownStation();
    }
}