package nextstep.subway.domain;

import nextstep.subway.exception.IllegalDeleteSectionException;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private static int MINIMAL_DELETE_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void init(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        sections.forEach(section -> {
            stations.add(section.getDownStation());
            stations.add(section.getUpStation());
        });
        return stations.stream().distinct().collect(Collectors.toList());
    }
    public void delete(Section section) {
        if (sections.size() <= MINIMAL_DELETE_SIZE) {
            throw new IllegalDeleteSectionException();
        }
        sections.remove(section);
    }
    public void deleteLastSection() {
        sections.remove(sections.size()-1);
    }
    public List<Section> getSections() {
        return sections;
    }
}
