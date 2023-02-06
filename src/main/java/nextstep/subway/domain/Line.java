package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.EmptySectionException;
import nextstep.subway.exception.CannotDeleteSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Collection;

@Entity
public class Line {
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

    public Line(Long id, String name, String color) {
        this.id = id;
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

    public List<Station> getAllStations() {
        return sections.stream()
                .map(section -> List.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        validateSection(section);
        sections.add(section);
    }

    private void validateSection(Section section) {
        if (Objects.equals(section.getUpStation(), section.getDownStation())) {
            throw new BadRequestException("UpStation and DownStation are same.");
        }

        if (sections.isEmpty()) {
            sections.add(section);
        }

        if (sections.stream().anyMatch(it -> it.isDownStation(section.getDownStation()))) {
            throw new BadRequestException("DownStation is already registered in section.");
        }

        if (!Objects.equals(getDownStation(), section.getUpStation())) {
            throw new BadRequestException("UpStation must be downStation of this line.");
        }
    }

    public Station getDownStation() {
        Section lastSection = getLastSection();
        return lastSection.getDownStation();
    }

    private Section getLastSection() {
        int size = sections.size();
        if (size < 1) {
            throw new EmptySectionException("Line has no section.");
        }
        return sections.get(size - 1);
    }

    public static final int MIN_SECTION_SIZE_OF_LINE = 1;

    public void removeSection(Station deleteStation) {
        if (sections.size() == MIN_SECTION_SIZE_OF_LINE) {
            throw new CannotDeleteSectionException("Line has only 1 section");
        }
        if (!getLastSection().isDownStation(deleteStation)) {
            throw new CannotDeleteSectionException("Station that trying to delete is not downStation of this line.");
        }
        sections.remove(getLastSection());
    }
}
