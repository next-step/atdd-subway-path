package nextstep.subway.line;

import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    protected LineSections() {
    }



    public Optional<Section> find(long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId() == stationId)
                .findFirst();
    }

    public void remove(Section deleteSection) {
        Section lastSection = this.sections.get(sections.size() - 1);
        if (lastSection.equals(deleteSection)) {
            this.sections = sections.subList(0, sections.size() - 1);
            return;
        }
        throw new IllegalStateException("하행 종점만 삭제 가능");
    }

    public boolean deletable() {
        return this.sections.size() > 1;
    }


    public void add(Section newSection) {

        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        Section last = this.sections.get(sections.size() - 1);
        if (last.getDownStation().equals(newSection.getUpStation())) {
            this.sections.add(newSection);
            return;
        }

        if (this.sections.stream().anyMatch(asis -> asis.equalSection(newSection))) {
            throw new DuplicateSectionException(newSection.toString());
        }
        Section asis = this.sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new StationNotFoundException(newSection.getUpStation().toString()));

        if (asis.equals(newSection)) {
            throw new DuplicateSectionException(newSection.toString());
        }

        int pos = this.sections.indexOf(asis);
        this.sections.remove(pos);
        sections.addAll(pos, asis.divide(newSection));
    }

    public List<Station> getStations() {
        Set<Station> set = new LinkedHashSet<>();
        for (Section section : sections) {
            set.add(section.getUpStation());
            set.add(section.getDownStation());
        }
        return List.copyOf(set);
    }
}
