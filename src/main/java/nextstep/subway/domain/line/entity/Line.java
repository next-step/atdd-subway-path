package nextstep.subway.domain.line.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.station.entity.Station;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.common.exception.ErrorMessage;

import javax.persistence.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
        section.setLine(this);
    }

    public Station getUpStation() {
        return sections.get(0).getUpStation();
    }

    public Station getDownStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }

    public void changeName(String name) {
        if (name == null) {
            return;
        }
        if (name.isEmpty()) {
            return;
        }
        if (name.isBlank()) {
            return;
        }
        this.name = name;
    }

    public void changeColor(String color) {
        if (color == null) {
            return;
        }
        if (color.isEmpty()) {
            return;
        }
        if (color.isBlank()) {
            return;
        }
        this.color = color;
    }

    public void add(Section section) {
        if (!this.getDownStation().equals(section.getUpStation())) {
            throw new InvalidParameterException(ErrorMessage.잘못된_상행역);
        }

        if (this.contains(section.getDownStation())) {
            throw new CustomException.Conflict(ErrorMessage.이미_포함된_하행역);
        }
        this.sections.add(section);
        section.setLine(this);
    }


    public void remove(Long stationId) {
        Section lastSection = this.sections.get(this.sections.size()-1);

        if (!lastSection.getDownStation().getId().equals(stationId)) {
            throw new InvalidParameterException(ErrorMessage.잘못된_하행역);
        }

        if(this.sections.size() == 1) {
            throw new InvalidParameterException(ErrorMessage.유일한구간);
        }

        this.sections.remove(lastSection);
        lastSection.setLine(null);
    }

    public boolean contains(Station station) {
        return this.sections.stream().anyMatch(section -> section.getUpStation().equals(station));
    }

    public List<Station> getStations () {
        List<Station> stations = new ArrayList<>();
        stations.add(this.getUpStation());
        this.sections.forEach(section -> stations.add(section.getDownStation()));
        return stations;
    }

}
