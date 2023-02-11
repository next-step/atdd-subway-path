package nextstep.subway.domain;

import nextstep.subway.common.AddTypeEnum;
import nextstep.subway.exception.SubwayRestApiException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.AddTypeEnum.FRONT_ADD_SECTION;
import static nextstep.subway.common.AddTypeEnum.MIDDLE_ADD_SECTION;
import static nextstep.subway.exception.ErrorResponseEnum.*;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void addSection(AddTypeEnum addTypeEnum, Section section) {
        isValidationSection(section);

        if (FRONT_ADD_SECTION.equals(addTypeEnum)) {
            Section standardSection = this.getSections().stream().filter(a -> section.getDownStation().equals(a.getUpStation())).findFirst().orElseThrow(IllegalArgumentException::new);

            this.getSections().add(this.getSections().indexOf(standardSection), section);
            return;
        }

        if (MIDDLE_ADD_SECTION.equals(addTypeEnum)) {
            Section standardSection = this.getSections().stream().filter(a -> section.getUpStation().equals(a.getUpStation())).findFirst().orElseThrow(IllegalArgumentException::new);

            isValidationSectionDistance(standardSection, section);

            int standardIndex = this.getSections().indexOf(standardSection);

            this.getSections().set(standardIndex, new Section(this, standardSection.getUpStation(), section.getDownStation(), section.getDistance()));
            this.getSections().add(standardIndex+1, new Section(this, section.getDownStation(), standardSection.getDownStation(), standardSection.getDistance()-section.getDistance()));

            return;
        }

        this.getSections().add(section);
    }

    private void isValidationSectionDistance(Section standardSection, Section section) {
        if (standardSection.getDistance() <= section.getDistance()) {
            throw new SubwayRestApiException(ERROR_ADD_SECTION_INVAILD_DISTANCE);
        }
    }

    private void isValidationSection(Section section) {
        if (this.getSections().size() > 0 && isExistUpOrDownStation(section)){
            throw new SubwayRestApiException(ERROR_ADD_SECTION_INVAILD_STATION);
        }
    }

    private boolean isExistUpOrDownStation (Section section) {
        int existStationCnt = this.getStations().stream().filter(a -> a.equals(section.getUpStation())
                || a.equals(section.getDownStation())).collect(Collectors.toList()).size();
        return existStationCnt != 1;
    }

    public void removeSection(Station station) {
        if (!this.getSections().get(this.getSections().size() - 1).getDownStation().equals(station)) {
            throw new SubwayRestApiException(ERROR_DELETE_SECTION_INVAILD_STATION);
        }

        this.getSections().remove(this.getSections().size() - 1);
    }

    public List<Station> getStations() {
        List<Station> downStations = this.getSections().stream().map(Section::getDownStation).collect(Collectors.toList());
        Section topSection = this.getSections().stream().filter(a -> downStations.contains(a.getUpStation()) == false).findFirst().orElse(null);

        List<Station> stations = new ArrayList<>();
        stations.add(topSection.getUpStation());

        Station downStation = topSection.getDownStation();

        for (int i = 0; i < this.getSections().size(); i++) {
            Station tempStation = downStation;
            stations.add(downStation);

            Section section = this.getSections().stream().filter(a -> tempStation.equals(a.getUpStation())).findFirst().orElse(null);

            if (null == section) {
                break;
            }

            downStation = section.getDownStation();
        }

        return stations;
    }
}
