package nextstep.subway.domain;

import nextstep.subway.domain.exception.SectionExceptionMessages;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @ManyToOne
    private Station firstStation;

    @ManyToOne
    private Station lastStation;

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

    public boolean hasSections() {
        return sections.size() > 0;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            firstStation = section.getUpStation();
            lastStation = section.getDownStation();
            return;
        }

        // 상행 종점
        if (isFirstSection(section)) {
            sections.add(0, section);
            firstStation = section.getUpStation();
            return;
        }

        // 하행 종점
        if (isLastSection(section)) {
            sections.add(section);
            lastStation = section.getDownStation();
            return;
        }

        // 사이
        addBetweenStations(section);
    }

    public int getSectionsCount() {
        return sections.size();
    }

    public Section getFirstSection() {
        if (sections.isEmpty()) {
            throw new NoSuchElementException();
        }

        if (sections.size() == 1) {
            return sections.get(0);
        }

        return sections.stream().filter(sec -> isFirstStation(sec.getUpStation()) && !isLastStation(sec.getDownStation()))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();

        Section currSection = getFirstSection();
        while (currSection != null) {
            stations.add(currSection.getUpStation());
            stations.add(currSection.getDownStation());
            currSection = nextSection(currSection);
        }

        return new ArrayList<>(stations);
    }

    private Section nextSection(Section currSection) {
        // 다음 구간 : 현재 구간의 하행역이 상행역인 구간
        return sections.stream().filter(sec -> sec.getUpStation().equals(currSection.getDownStation())).findFirst().orElse(null);
    }

    public void removeSection(Station station) {
        if (!isLastStation(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public int getLength() {
        if (sections.isEmpty()) {
            return 0;
        }

        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    private boolean isFirstStation(Station station) {
        return firstStation.equals(station);
    }

    private boolean isLastStation(Station station) {
        return lastStation.equals(station);
    }

    private boolean isFirstSection(Section section) {
        return isFirstStation(section.getDownStation());
    }

    private boolean isLastSection(Section section) {
        return isLastStation(section.getUpStation());
    }

    private void addBetweenStations(Section section) {
        Section originSection =  sections.stream().filter(sec -> sec.getUpStation().equals(section.getUpStation()) || sec.getDownStation().equals(section.getDownStation()))
                .findFirst().orElseThrow(NoSuchElementException::new);

        if (section.getDistance() >= originSection.getDistance()) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.INVALID_DISTANCE);
        }

        removeSection(originSection.getDownStation());

        sections.add(section);

        int newDistance = originSection.getDistance() - section.getDistance();
        if (originSection.getDownStation().equals(section.getDownStation())) {
            sections.add(new Section(originSection.getLine(), originSection.getUpStation(), section.getUpStation(), newDistance));
            return;
        }

        if (originSection.getUpStation().equals(section.getUpStation())) {
            sections.add(new Section(originSection.getLine(), section.getDownStation(), originSection.getDownStation(), newDistance));
            return;
        }
    }

}
