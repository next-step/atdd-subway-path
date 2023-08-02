package nextstep.subway.domain;

import lombok.NoArgsConstructor;
import nextstep.subway.domain.add.SectionAddStrategy;
import nextstep.subway.domain.add.SectionAddType;
import nextstep.subway.domain.delete.SectionDeleteStrategy;
import nextstep.subway.domain.delete.SectionDeleteType;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;
import org.jgrapht.graph.WeightedMultigraph;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(int index, Section section) {
        sections.add(index, section);
    }

    public void add(Section section) {
        sections.add(section);
    }

    public int indexOf(Section section) {
        return sections.indexOf(section);
    }

    public boolean containsAtUpStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.equalUpStation(station));
    }

    public boolean containsAtDownStation(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.equalDownStation(downStation));
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());

        return stations;
    }

    public void addSection(Section section) {
        Stations stations = new Stations(getStations());
        if (!stations.empty() && stations.doesNotContainAll(section.getUpStation(), section.getDownStation())) {
            throw new SectionAddException(ErrorType.STATIONS_NOT_EXIST_IN_LINE);
        }
        if (stations.containsAll(section.getUpStation(), section.getDownStation())) {
            throw new SectionAddException(ErrorType.STATIONS_EXIST_IN_LINE);
        }
        SectionAddStrategy strategy = SectionAddType.find(this, section);
        strategy.add(this, section);
    }

    public boolean remainOneSection() {
        return sections.size() == 1;
    }

    public void remove(Station station) {
        Stations stations = new Stations(getStations());
        SectionDeleteStrategy strategy = SectionDeleteType.find(stations, station);
        strategy.delete(this, station);
    }

    public void remove(Section lastSection) {
        sections.remove(lastSection);
    }

    public Section findFirst() {
        return sections.get(0);
    }

    public Section findLast() {
        return sections.get(sections.size() - 1);
    }

    public List<Section> findIncluded(Station station) {
        return sections.stream()
                .filter(section -> section.has(station))
                .collect(Collectors.toList());
    }

    public boolean empty() {
        return sections.isEmpty();
    }

    public void addEdges(WeightedMultigraph graph) {
        sections.forEach(section -> section.addEdge(graph));
    }
}
