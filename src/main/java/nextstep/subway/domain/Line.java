package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        validateDistance(distance);

        boolean isUpStationExist = isExistInLine(upStation);
        boolean isDownStationExist = isExistInLine(downStation);

        validateSection(isUpStationExist, isDownStationExist);

        if (isUpStationExist) {
            updateUpStationBetweenSection(upStation, downStation, distance);
        }

        if (isDownStationExist) {
            updateDownStationBetweenSection(upStation, downStation, distance);
        }

        addNewSection(upStation, downStation, distance);
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 길이가 0 이거나 음수일 수 없습니다.");
        }
    }

    private void addNewSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    private void updateUpStationBetweenSection(Station upStation, Station downStation, int distance) {
        this.sections.stream()
                .filter(section -> section.equalUpStation(upStation))
                .findFirst()
                .ifPresent(section -> section.updateUpStation(downStation, distance));
    }

    private void updateDownStationBetweenSection(Station upStation, Station downStation, int distance) {
        this.sections.stream()
                .filter(section -> section.equalDownStation(downStation))
                .findFirst()
                .ifPresent(section -> section.updateDownStation(upStation, distance));
    }

    private void validateSection(boolean isUpStationExist, boolean isDownStationExist) {
        if (this.sections.isEmpty()) {
            return;
        }

        if (isUpStationExist && isDownStationExist) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }

        if (!isUpStationExist && !isDownStationExist) {
            throw new IllegalArgumentException("등록할 구간의 상행역과 하행역이 노선에 포함되어 있지 않아 등록할 수 없습니다.");
        }
    }

    private boolean isExistInLine(Station station) {
        return getStations().stream().anyMatch(station::equals);
    }

    public void removeSection(Station station) {
        if (!this.getSections().get(this.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        this.getSections().remove(this.getSections().size() - 1);
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Station station = findFirstUpStation();
        stations.add(station);

        while (isPresentNextSection(station)) {
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }

    private Section findNextSection(Station station) {
        return this.sections.stream()
                .filter(section -> section.equalUpStation(station))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Station findFirstUpStation() {
        Station upStation = this.sections.get(0).getUpStation();

        while (isPresentPrevSection(upStation)) {
            Section prevSection = findPrevSection(upStation);
            upStation = prevSection.getUpStation();
        }

        return upStation;
    }

    private boolean isPresentNextSection(Station station) {
        return this.sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    private boolean isPresentPrevSection(Station upStation) {
        return this.sections.stream()
                .anyMatch(section -> section.equalDownStation(upStation));
    }

    private Section findPrevSection(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.equalDownStation(upStation))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
