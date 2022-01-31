package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
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

    public void addSection(Section section) {
        if (isAllMatchOrNoneMach(section)) {
            throw new IllegalArgumentException("구간을 생성할 수 없습니다.");
        }
        Section startSection = getStartSection();
        Section endSection = getEndSection();
        addSectionOnCenter(section);
        addSectionStartOrEnd(section, startSection, endSection);
    }

    private void addSectionStartOrEnd(Section section, Section startSection, Section endSection) {
        sections.stream()
                .filter((s) -> section.getDownStation().equals(startSection.getUpStation()) || section.getUpStation().equals(endSection.getDownStation()))
                .findFirst()
                .ifPresent((s)->{
                    sections.add(section);
                });
    }

    private void addSectionOnCenter(Section section) {
        sections.stream()
                .filter(findSection -> findSection.getUpStation().equals(section.getUpStation()))
                .findFirst()
                .ifPresent((findSection) -> {
                    if (section.getDistance() >= findSection.getDistance()) {
                        throw new IllegalArgumentException("구간을 생성할 수 없습니다.");
                    }
                    sections.add(new Section(this, section.getUpStation(), section.getDownStation(), section.getDistance()));
                    sections.add(new Section(this, section.getDownStation(),findSection.getDownStation(), findSection.getDistance() - section.getDistance()));
                    sections.remove(findSection);
                });
    }

    public void removeSection(Station station) {
        // 1. 구간이 하나 뿐일 경우 IllegalArgumentException 을 내보낸다.
        // 2. 존재하지 않는 구간으로 삭제하려고 했을 경우 IllegalArgumentException 을 내보낸다.
        // 3. 삭제하는 역이 상행 종점 상행역일 경우 해당 구간을 삭제한다.
        // 4. 삭제하는 역이 하행 종점 하행역일 경우 해당 구간을 삭제한다.
        // 5. 삭제하는 역이 어떤 구간의 하행역이면서 상행역일 경우. 해당 역을 포함한 두 구간을 모두 삭제하며, 두 구간에 남은 역들을 연결하는 구간을 신설한다.
        // 이 때 두 구간의 거리는 삭제한 두 구간의 합으로 결정한다.

        //1
        if (sections.size() < 2) {
            throw new IllegalArgumentException("삭제할 수 있는 구간이 존재하지 않습니다.");
        }

        //2
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("삭제할 수 있는 구간이 존재하지 않습니다.");
        }

        //3 ,4
        Section startSection = getStartSection();
        Section endSection = getEndSection();
        sections.stream()
                .filter(section -> (section.equals(startSection) && station.equals(section.getUpStation())) || (section.equals(endSection) && station.equals(section.getDownStation())))
                .findFirst()
                .ifPresent(section -> {
                    sections.remove(section);
                });

        //5
        Section upStationSection = sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseGet(() -> null);

        Section downStationSection = sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseGet(() -> null);

        if (upStationSection != null && downStationSection != null) {
            Section newSection = new Section(this, upStationSection.getUpStation(), downStationSection.getDownStation(), upStationSection.getDistance() + downStationSection.getDistance());
            sections.add(newSection);
            sections.remove(upStationSection);
            sections.remove(downStationSection);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section section = getStartSection();
        stations.add(section.getUpStation());

        while (section != null) {
            Station downStation = section.getDownStation();
            Optional<Section> nextSection = sections.stream()
                    .filter(oldSection -> oldSection.getUpStation().equals(downStation))
                    .findFirst();

            stations.add(downStation);
            section = nextSection.orElseGet(() -> null);
        }
        return stations;
    }


    private boolean isAllMatchOrNoneMach(Section section) {
        return getStations().stream().noneMatch((s) -> s.equals(section.getUpStation()) || s.equals(section.getDownStation()))
                || getStations().stream().filter((s) -> s.equals(section.getUpStation()) || s.equals(section.getDownStation())).count() == 2;
    }

    private Section getStartSection() {
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(s -> s.getDownStation().equals(section.getUpStation())))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }

    private Section getEndSection() {
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(s -> s.getUpStation().equals(section.getDownStation())))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }


    public Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseGet(() -> null);
    }
}
