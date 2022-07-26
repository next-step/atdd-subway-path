package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class LineSection {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        List<Section> sortedSections = getSections();
        List<Station> stations = sortedSections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());
        stations.add(sortedSections.get(sortedSections.size()-1).getDownStation());
        return stations;
    }
    private Section getFirstSection() {
        List<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());
        upStations.removeAll(downStations);
        Station upStation = upStations.get(0);
        for (Section section : sections) {
            if (section.getUpStation().equals(upStation)) {
                return section;
            }
        }
        return null;
    }
    private Section getNextSection(Section now) {
        for (Section section : sections) {
            if (now.getDownStation().equals(section.getUpStation())) {
                return section;
            }
        }
        return null;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        if (section.getDownStation().equals(sections.get(0).getUpStation())) {
            sections.add(0,section);
            return;
        }
        if (section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())) {
            sections.add(section);
            return;
        }

        addMiddleSection(section);
    }

    private void addMiddleSection(Section section) {
        Section target = findSection(section);
        Station middleStation = section.getDownStation();
        Station endStation = target.getDownStation();
        target.changeDownStation(middleStation);
        section.changeUpStation(middleStation);
        section.changeDownStation(endStation);
        int index = findSectionIndex(section);
        sections.add(index+1,section);
    }

    private Section findSection(Section section) {
        List<Section> foundSections = sections.stream()
            .filter(v -> v.getUpStation().equals(section.getUpStation()))
            .collect(Collectors.toList());
        if (foundSections.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return foundSections.get(0);
    }
    private int findSectionIndex(Section section) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStation().equals(section.getUpStation())) {
                return i;
            }
        }
        return 0;
    }

    public void remove(String stationName) {
        sections.removeIf(v->v.getUpStation().getName().equals(stationName));
    }
    public void removeLast() {
        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        List<Section> sortedSection = new ArrayList<>();
        Section section = getFirstSection();
        sortedSection.add(section);
        while((section=getNextSection(section)) != null) {
            sortedSection.add(section);
        }
        return sortedSection;
    }

    public int size() {
        return sections.size();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
    public void checkArgument(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
    }
}
