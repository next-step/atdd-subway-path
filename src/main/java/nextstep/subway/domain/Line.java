package nextstep.subway.domain;

import org.springframework.util.ObjectUtils;

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

    @Embedded
    private final Sections sections = new Sections();

    protected Line() { }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this(name, color);
        sections.add(0, new Section(this, upStation, downStation, distance));
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void update(final String name, final String color) {
        if (!ObjectUtils.isEmpty(name)) {
            this.name = name;
        }
        if (!ObjectUtils.isEmpty(color)) {
            this.color = color;
        }
    }

    public boolean isSectionsEmpty() {

        return sections.isEmpty();
    }

    public void addSection(Station upStation, Station downStation, int distance) {

        sections.isExist(upStation, downStation);

        Optional<Section> sectionWithUpStation = sections.findSectionWithUpStation(upStation);

        if (sectionWithUpStation.isPresent()) {
            // 비교하기
            Section originalSection = sectionWithUpStation.get();
            addSectionByCondition(originalSection, upStation, downStation, distance);

            return ;
        }

        Section lastDownSection = sections.findLastDownSection();

        if (lastDownSection.isDownStation(upStation)) {
            sections.add(sectionSize(), new Section(this, upStation, downStation, distance));

            return;
        }

        Section lastUpSection = sections.findLastUpSection();

        if (lastUpSection.isUpStation(downStation)) {
            sections.add(0, new Section(this, upStation, downStation, distance));

            return;
        }
    }

    private void addSectionByCondition(Section originalSection, Station newUpStation, Station newDownStation, int newDistance) {

        int originalDistance = originalSection.getDistance();

        if (originalDistance == newDistance) {

            throw new RuntimeException("구간 사이 등록할 때, 길이가 같습니다.");
        }

        if (originalSection.haveStations(newUpStation, newDownStation)) {

            throw new RuntimeException("이미 두 역으로 생성된 존재하는 구간입니다.");
        }

        int index = sections.remove(originalSection.getDownStation());

        if (originalSection.getDistance() > newDistance) {
            sections.add(index, new Section(this, newUpStation, newDownStation, newDistance));
            sections.add(index + 1, new Section(
                    this,
                    newDownStation,
                    originalSection.getDownStation(),
                    originalDistance - newDistance));

            return ;
        }
        sections.add(index, new Section(this, newUpStation, originalSection.getDownStation(), originalDistance));
        sections.add(index + 1, new Section(this,
                originalSection.getDownStation(),
                newDownStation,
                newDistance - originalDistance));
    }

    public List<Station> getStations() {
        List<Station> allStations = new ArrayList<>();
        Section lastUpSection = findLastUpSection();
        allStations.add(lastUpSection.getUpStation());

        Optional<Section> nextSection = Optional.ofNullable(lastUpSection);

        while (nextSection.isPresent()) {
            Section section = nextSection.get();
            allStations.add(section.getDownStation());
            nextSection = findSectionWithUpStation(section.getDownStation());
        }

        return allStations;
    }

    private Optional<Section> findSectionWithUpStation(Station station) {

        return sections.findSectionWithUpStation(station);
    }

    public int sectionSize() {

        return sections.size();
    }

    public void deleteSection(Station station) {
        if (!isLastDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(station);
    }

    private boolean isLastDownStation(Station station) {
        return sections.isLastDownStation(station);
    }

    private Section findLastUpSection() {

        return sections.findLastUpSection();
    }

    public List<Section> getSections() {

        return sections.values();
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
}
