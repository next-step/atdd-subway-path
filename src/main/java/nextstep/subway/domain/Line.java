package nextstep.subway.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line {
    @Embedded
    private final Sections sections = new Sections();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, nullable = false)
    private String color;
    private Long upStationId;
    private Long downStationId;

    protected Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStation.getId();
        this.downStationId = downStation.getId();

        Section section = new Section(upStation, downStation, distance, this);
        this.sections.addEnd(section);
    }

    public Line(Long id ,String name, String color, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStation.getId();
        this.downStationId = downStation.getId();

        Section section = new Section(upStation, downStation, distance, this);
        this.sections.addEnd(section);
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

    public List<Section> getSections() {
        return this.sections.getSections();
    }

    public int totalDistance() {
        return this.sections.totalDistance();
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public void addSection(final Station upStation, final Station downStation, final int distance) {
        if (this.downStationId.equals(upStation.getId())) {
            // 끝에 추가
            this.sections.checkLineStationsDuplicate(downStation);
            this.sections.addEnd(new Section(upStation, downStation, distance, this));
            this.downStationId = downStation.getId();

        } else if (this.upStationId.equals(downStation.getId())) {
            // 앞에 추가
            this.sections.checkLineStationsDuplicate(upStation);
            this.sections.addFirst(new Section(upStation, downStation, distance, this));
            this.upStationId = upStation.getId();
        } else {
            //중간에 추가
            this.sections.checkLineStationsDuplicate(downStation);
            this.sections.addMiddle(upStation, downStation, distance, this);
        }
    }

    public void removeSection(final Long stationId) {
        deleteValidate(stationId);
        this.sections.removeSection(stationId);
    }

    private void deleteValidate(final Long stationId) {
        if (!this.downStationId.equals(stationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 수 없는 지하철 역 입니다.");
        }

        if (this.sections.count() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 수 없는 지하철 역 입니다.");
        }
    }

    public List<Station> getStations() {
        return this.sections.getStations(this.upStationId);
    }
}
