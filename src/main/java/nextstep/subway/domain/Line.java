package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    private Station downStation;

    public Line() {
    }

    private Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.upStation = upStation;
        this.downStation = downStation;
        sections.add(initSection(upStation, downStation, distance));
    }

    private Section initSection(Station upStation, Station downStation, int distance) {
        return Section.of(this, upStation, downStation, distance);
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    /* getter */
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
        return sections;
    }

    /* 구간 추가 */
    public void addSection(Section section) {
        if (isUpStation(section.getDownStation())) {
            this.upStation = section.getUpStation();
        }

        if (isDownStation(section.getUpStation())) {
            this.downStation = section.getDownStation();
        }

        insertSection(section);
    }

    private void insertSection(Section section) {
        // 새로운 구간의 상행역과 기존 구간의 상행역이 같다면
        // 새로운 구간의 하행역을 상행역으로, 기존 구간의 하행역을 하행역으로 갖는 구간을 생성하고
        // 기존 구간을 삭제하면서
        // 입력된 구간을 추가한다.
        sections.stream()
                .filter(oriSection -> oriSection.isUpStation(section.getUpStation())
                        && oriSection.isNotDownStation(section.getDownStation()))
                .findFirst()
                .ifPresent(oriSection -> {
                    pushSection(section.getDownStation(), oriSection.getDownStation(),
                            oriSection.getDistance() - section.getDistance());
                    sections.remove(oriSection);
                });


        // 새로운 구간의 하행역과 기존 구간의 하행역이 같다면
        // 새로운 구간의 상행역을 하행역으로, 기존 구간의 상행역을 상행역으로 갖는 구간을 생성하고
        // 기존 구간을 삭제하면서
        // 입력된 구간을 추가한다.
        sections.stream()
                .filter(oriSection -> oriSection.isDownStation(section.getDownStation())
                        && oriSection.isNotUpStation(section.getUpStation()))
                .findFirst()
                .ifPresent(oriSection -> {
                    pushSection(oriSection.getUpStation(), section.getUpStation(),
                            oriSection.getDistance() - section.getDistance());
                    sections.remove(oriSection);
                });

        sections.add(section);
    }

    private void pushSection(Station upStation, Station downStation, int newDistance) {
        sections.add(Section.of(this, upStation, downStation, newDistance));
    }

    /* 갖고있는 지하철역 리스트 반환 */
    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        List<Section> tmpSections = new ArrayList<>(sections);
        Station tmpStation = upStation;

        while (!tmpSections.isEmpty()) {
            for (Section section : tmpSections) {
                if (tmpStation.equals(section.getUpStation())) {
                    stations.add(section.getUpStation());
                    tmpStation = section.getDownStation();
                    tmpSections.remove(section);
                    break;
                }
            }
        }

        stations.add(tmpStation);
        return stations.stream().distinct().collect(Collectors.toList());
    }

    /* 노선 정보 변경 */
    public void update(String name, String color) {
        if (name != null && !this.name.equals(name)) {
            this.name = name;
        }
        if (color != null && !this.color.equals(color)) {
            this.color = color;
        }
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
    }
}
