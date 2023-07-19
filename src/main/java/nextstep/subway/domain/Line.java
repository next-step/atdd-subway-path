package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station firstUpStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station lastDownStation;

    @Embedded
    private Sections sections = new Sections();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void updateInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void updateFirstUpStation(Station firstUpStation){
        this.firstUpStation = firstUpStation;
    }

    public void updateLastDownStation(Station lastDownStation){
        this.lastDownStation = lastDownStation;
    }

    public void addSection(Section newSection){
        sections.addSection(this,newSection);
    }

    public void removeSection(Station station){
        sections.removeSection(this,station);
    }

    public List<Station> getOrderedStationList(){
        return sections.getOrderedStationList(this);
    }


}
