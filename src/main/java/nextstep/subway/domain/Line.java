package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
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

    public void removeSection(Station station) {
        // 네이밍... 괜찮은 걸까요? 혹시 이런 상황에서 추천 해 주실만한 메소드 네이밍이 있을까요?
        // 이런 private method 를 인수테스트에서 Steps 분리하듯이 해당 Entity 에 종속적인 Util 로 나누어도 괜찮다고 생각하시나요?
        // 약간 public method 를 제외하고는 전부 분리해 내고 싶은데...
        validRemove(station);
        removeTerminus(station);
        removeMiddleComponent(station);
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


    public Section getSectionByUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(upStation))
                .findFirst()
                .orElseGet(() -> null);
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

    private void removeTerminus(Station station) {
        sections.stream()
                .filter(section -> (section.equals(getStartSection()) && station.equals(section.getUpStation())) || (section.equals(getEndSection()) && station.equals(section.getDownStation())))
                .findFirst()
                .ifPresent(section -> {
                    sections.remove(section);
                });
    }

    private void removeMiddleComponent(Station station) {
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

    private void validRemove(Station station) {
        if (sections.size() < 2 || !getStations().contains(station)) {
            throw new IllegalArgumentException("삭제할 수 있는 구간이 존재하지 않습니다.");
        }
    }

}
