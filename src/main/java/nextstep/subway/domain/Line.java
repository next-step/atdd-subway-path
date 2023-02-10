package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    public static final String EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED = "지하철노선은 1개 구간 이하로 구성될 수 없습니다.";
    public static final String EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION = "해당 노선의 하행종점역만 제거할 수 있습니다.";

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

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Station station) {
        checkRemovableStation(station);

        sections.remove(sections.size() - 1);
    }

    private void checkRemovableStation(Station station) {
        verifyMinimumSectionCount();
        verifyTailStation(station);
    }

    private void verifyMinimumSectionCount() {
        if (sections.size() == 1) {
            throw new IllegalStateException(EXCEPTION_MESSAGE_MINIMUM_ONE_SECTION_REQUIRED);
        }
    }

    private void verifyTailStation(Station station) {
        Station tailStation = getTailStation();
        if (!tailStation.equals(station)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_CAN_REMOVE_TAIL_STATION);
        }
    }

    private Station getTailStation() {
        Section tailSection = getTailSection();
        return tailSection.getDownStation();
    }

    private Section getTailSection() {
        return sections.get(sections.size() - 1);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            if (sections.indexOf(section) == sections.size() - 1) {
                stations.add(section.getDownStation());
            }
        });

        return stations;
    }
}
