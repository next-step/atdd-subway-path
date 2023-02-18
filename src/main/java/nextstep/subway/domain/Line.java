package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        validateDistance(distance);

        boolean isUpStationExist = isExistInLine(upStation);
        boolean isDownStationExist = isExistInLine(downStation);

        validateAddSection(isUpStationExist, isDownStationExist);

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

    private void validateAddSection(boolean isUpStationExist, boolean isDownStationExist) {
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

    public boolean isExistInLine(Station station) {
        return getStations().stream().anyMatch(station::equals);
    }

    public void removeSection(Station station) {
        validateRemoveSection();

        Optional<Section> firstSection =  this.sections.findSectionByDownStation(station);
        Optional<Section> secondSection =  this.sections.findSectionByUpStation(station);

        validateStationInLine(firstSection.isEmpty() && secondSection.isEmpty());

        if (firstSection.isPresent() && secondSection.isPresent()) {
            this.sections.addMergeSection(this, firstSection.get(), secondSection.get());
        }

        firstSection.ifPresent(section -> this.sections.removeSection(section));
        secondSection.ifPresent(section -> this.sections.removeSection(section));
    }

    private void validateRemoveSection() {
        if (this.sections.isOnlyOne()) {
            throw new IllegalStateException("등록된 구간이 딱 한개면 구간을 삭제할 수 없습니다.");
        }
    }

    private void validateStationInLine(boolean isStationNotInLine) {
        if (isStationNotInLine) {
            throw new IllegalArgumentException("노선에 등록되어있지 않은 역은 제거할 수 없습니다.");
        }
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
