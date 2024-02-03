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

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStation.getId();
        this.downStationId = downStation.getId();

        Section section = new Section(upStation, downStation, distance, this);
        this.sections.add(section);
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

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(final String color) {
        this.color = color;
    }

    public void addSection(final Station upStation, final Station downStation, final Section section) {
        registerValidate(upStation, downStation);
        changeDownStation(downStation);

        this.sections.add(section);
    }

    private void registerValidate(final Station upStation, final Station downStation) {
        checkEqualsLineDownStation(upStation);

        this.sections.checkLineStationsDuplicate(downStation);
    }

    private void checkEqualsLineDownStation(final Station upStation) {
        if (!this.downStationId.equals(upStation.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "노선의 하행종점역과 등록하려는 구간의 상행역이 다릅니다.");
        }
    }

    private void changeDownStation(final Station station) {
        this.downStationId = station.getId();
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
}
