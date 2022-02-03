package nextstep.subway.domain;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Section> sections = new LinkedList<>();

    public void init(Section section) {
        section.setLine(this);
    }

    public void addSection(Section section) {
        verifyCanBeAdded(section);

        var stations = getStations();
        if (stations.get(0).equals(section.getDownStation())) {
            section.setLine(this);
            return;
        }

        if (stations.get(stations.size() - 1).equals(section.getUpStation())) {
            section.setLine(this);
            return;
        }

        addSectionToBetween(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        var ret = new ArrayList<Station>();
        var top = firstSection();
        ret.add(top.getUpStation());

        var curSection = top;
        while (curSection != null) {
            ret.add(curSection.getDownStation());
            curSection = nextSection(curSection);
        }

        return ret;
    }

    private Section firstSection() {
        var downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(it -> !downStations.contains(it.getUpStation()))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }

    private Section nextSection(Section section) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findAny()
                .orElse(null);
    }

    private void addSectionToBetween(Section section) {
        var betweenSectionUp = sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findAny()
                .orElse(null);

        if (betweenSectionUp != null) {
            if (betweenSectionUp.getDistance() <= section.getDistance()) {
                throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같아서 추가할 수 없습니다.");
            }

            section.setLine(this);
            var newSection = new Section(
                    section.getDownStation(),
                    betweenSectionUp.getDownStation(),
                    betweenSectionUp.getDistance() - section.getDistance()
            );
            newSection.setLine(this);

            sections.remove(betweenSectionUp);
            return;
        }

        var betweenSectionDown = sections.stream()
                .filter(it -> it.getDownStation().equals(section.getDownStation()))
                .findAny()
                .orElse(null);

        if (betweenSectionDown != null) {
            if (betweenSectionDown.getDistance() <= section.getDistance()) {
                throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같아서 추가할 수 없습니다.");
            }

            section.setLine(this);
            var newSection = new Section(
                    betweenSectionDown.getUpStation(),
                    section.getUpStation(),
                    betweenSectionDown.getDistance() - section.getDistance()
            );
            newSection.setLine(this);

            sections.remove(betweenSectionDown);
        }
    }

    protected Line() {
    }

    public Line(String name, String color) {
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

    public List<Section> getSections() {
        return sections;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void remove(Station station) {
        verifyCanBeDeleted(station);
        sections.remove(sections.size() - 1);
    }

    private void verifyCanBeAdded(Section section) {
        if (sections.stream().filter(it -> it.hasStation(section.getUpStation())).findAny().isEmpty()
                && sections.stream().filter(it -> it.hasStation(section.getDownStation())).findAny().isEmpty()) {
            throw new IllegalArgumentException("상행역과 하행역 모두 등록되어 있지 않아서 추가할 수 없습니다.");
        }

        if (sections.stream().anyMatch(it ->
                it.getUpStation().equals(section.getUpStation())
                        && it.getDownStation().equals(section.getDownStation()))
        ) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어 있어서 추가할 수 없습니다.");
        }
    }

    private void verifyCanBeDeleted(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalStateException("노선에 구간이 부족하여 역을 삭제할 수 없습니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException("삭제하려는 역은 해당 노선 마지막 구간의 하행역이 아닙니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        var line = (Line) obj;
        return StringUtils.equals(this.name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
