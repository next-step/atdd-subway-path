package nextstep.subway.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

import nextstep.subway.exception.*;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

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
        return sections.getSections();
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            // 상행, 하행 둘다 노선에 있을 때 예외 처리
            if (sections.containsStations(List.of(section.getUpStation(), section.getDownStation()))) {
                throw new SectionAlreadyCreateStationException();
            }
            if (!sections.checkExistStation(section.getUpStation()) && !sections.checkExistStation(section.getDownStation())) {
                throw new SectionDoesNotHaveAlreadyCreateStationException();
            }

            // 추가하행역이 노선의 상행역인 경우
            if (isUpStationId(section.getDownStation().getId())) {
                sections.addLast(section);
            } else if (isDownStaionId(section.getUpStation().getId())) {
                sections.addLast(section);
            } else {
                Optional<Section> optionalSectionUp = getSectionHasSameUpStation(section.getUpStation());
                if (optionalSectionUp.isPresent()) {

                    Section existSection = optionalSectionUp.get();
                    validateInsertSectionSize(section, existSection);
                    sections.remove(existSection);
                    sections.addLast(section);
                    sections.addLast(new Section(this, section.getDownStation(), existSection.getDownStation(),
                            existSection.getDistance() - section.getDistance()));
                } else {
                    Optional<Section> optionalSectionDown = getSectionHasSameDownStation(section.getDownStation());
                    if (optionalSectionDown.isPresent()) {
                        Section existSection = optionalSectionDown.get();
                        validateInsertSectionSize(section, existSection);
                        sections.remove(existSection);
                        sections.addLast(section);
                        sections.addLast(new Section(this, existSection.getUpStation(), section.getUpStation(),
                                existSection.getDistance() - section.getDistance()));
                    }
                }
            }
        } else {
            sections.addLast(section);
        }
    }

    private static void validateInsertSectionSize(Section section, Section existSection) {
        if (existSection.getDistance() <= section.getDistance()) {
            throw new SectionInsertDistanceTooLargeException();
        }
    }

    private Optional<Section> getSectionHasSameDownStation(Station downStation) {
        return getSections().stream()
                .filter(s -> s.getDownStation().equals(downStation))
                .findFirst();
    }

    private Optional<Section> getSectionHasSameUpStation(Station upStation) {
        return getSections().stream()
                .filter(s -> s.getUpStation().equals(upStation))
                .findFirst();
    }

    private boolean doesNotContainStationId(Long id) {
        List<Station> stations = getStations();
        return stations.stream()
                .map(Station::getId)
                .anyMatch((stationId) -> stationId.equals(id));
    }

    private boolean isDownStaionId(Long id) {
        return sections.getLastSection().getDownStation().getId().equals(id);
    }

    private boolean isUpStationId(long id) {
        return sections.getFirstSection().getUpStation().getId().equals(id);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void removeSection(Station station) {
        if (!sections.getLastSection().getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.removeLastSection();
    }

    public void changeNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isEmptySection() {
        return this.sections.isEmpty();
    }

    public boolean checkExistStation(Station station) {
        return sections.checkExistStation(station);
    }
}
