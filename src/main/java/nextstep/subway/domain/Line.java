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

    @Embedded
    private Sections sections = new Sections();

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

    public String getColor() {
        return color;
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getColor() != null) {
            this.color = line.getColor();
        }
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        validateDistance(distance);

        boolean isUpStationExist = isExistInLine(upStation);
        boolean isDownStationExist = isExistInLine(downStation);

        validateSection(isUpStationExist, isDownStationExist);

        if (isUpStationExist) {
            this.sections.updateUpStationBetweenSection(upStation, downStation, distance);
        }

        if (isDownStationExist) {
            this.sections.updateDownStationBetweenSection(upStation, downStation, distance);
        }

        this.sections.addNewSection(new Section(this, upStation, downStation, distance));
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 길이가 0 이거나 음수일 수 없습니다.");
        }
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
        Station station = this.sections.findFirstUpStation();
        stations.add(station);

        while (this.sections.isPresentNextSection(station)) {
            Section nextSection = this.sections.findNextSection(station);
            station = nextSection.getDownStation();
            stations.add(station);
        }
        return stations;
    }
}
