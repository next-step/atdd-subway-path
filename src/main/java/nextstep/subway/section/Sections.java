package nextstep.subway.section;


import nextstep.subway.exception.InvalidInputException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Embeddable
public class Sections implements Iterable<Section> {

    @OneToMany(cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

    public int size() {
        return sections.size();
    }

    public Stream<Section> stream() {
        return sections.stream();
    }

    public void initSection(Section section) {
        sections.add(section);
    }

    public int getFirstSectionDistance() {
        return sections.get(0).getDistance();
    }

    public int getLastSectionDistance() {
        return sections.get(sections.size() - 1).getDistance();
    }

    public Station getFirstUpstation() {
        return sections.get(0).getUpstation();
    }

    public Station getLastDownstation() {
        return sections.get(sections.size() - 1).getDownstation();
    }

    public void addFirstSection(Section newSection) {
        if (sections.stream().anyMatch(section ->
                section.getDownstation().getId().equals(newSection.getUpstation().getId()) ||
                        section.getUpstation().getId().equals(newSection.getUpstation().getId()))) {
            throw new InvalidInputException("새로운 구간의 하행역은 이미 노선에 존재하는 역이면 안 됩니다.");
        }

        sections.add(0, newSection);
    }

    public void addSection(Section newSection, int lineDistance) {
        // newSection의 upstation, downstation 둘 다 노선에 등록되어있는 거면 안 됨
        boolean upstationExists = sections.stream().anyMatch(section ->
                section.getUpstation().getId().equals(newSection.getUpstation().getId()) ||
                        section.getDownstation().getId().equals(newSection.getUpstation().getId()));
        boolean downstationExists = sections.stream().anyMatch(section ->
                section.getUpstation().getId().equals(newSection.getDownstation().getId()) ||
                        section.getDownstation().getId().equals(newSection.getDownstation().getId()));

        if (upstationExists && downstationExists) {
            throw new InvalidInputException("새로운 구간의 상행역과 하행역 둘 다 이미 노선에 등록되어 있습니다.");
        }

        if (!upstationExists && !downstationExists) {
            throw new InvalidInputException("새로운 구간은 기존 노선의 역과 최소 하나 이상 연결되어야 합니다.");
        }

        if (upstationExists) {
            for (int i = 0; i < sections.size(); i++) {
                Section currentSection = sections.get(i);
                if (currentSection.getUpstation().getId().equals(newSection.getUpstation().getId())) {
                    currentSection.setUpstation(newSection.getDownstation());
                    currentSection.setDistance(lineDistance - newSection.getDistance());
                    sections.add(i, newSection);
                    return;
                }
            }
        } else {
            for (int i = 0; i < sections.size(); i++) {
                Section currentSection = sections.get(i);
                if (currentSection.getDownstation().getId().equals(newSection.getDownstation().getId())) {
                    currentSection.setDownstation(newSection.getUpstation());
                    currentSection.setDistance(lineDistance - newSection.getDistance());
                    sections.add(i + 1, newSection);
                    return;
                }
            }
        }

    }

    public void addLastSection(Section newSection) {
        Station lastDownstation = getLastDownstation();
        if (!Objects.equals(lastDownstation.getId(), newSection.getUpstation().getId())) {
            throw new InvalidInputException("해당 노선의 하행 종점역과 새로운 구간의 상행역이 일치해야 합니다.");
        }

        if (sections.stream().anyMatch(section ->
                section.getDownstation().getId().equals(newSection.getDownstation().getId()) ||
                        section.getUpstation().getId().equals(newSection.getDownstation().getId()))) {
            throw new InvalidInputException("새로운 구간의 하행역은 이미 노선에 존재하는 역이면 안 됩니다.");
        }

        sections.add(newSection);
    }

    public void removeFirstSection() {
        sections.remove(0);
    }
    public void removeLastSection() {
        sections.remove(sections.size() - 1);
    }

    public void removeSection(Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Section currentSection = sections.get(i);
            if (currentSection.getDownstation().getId().equals(station.getId())) {
                Section nextSection = sections.get(i + 1);
                currentSection.setDownstation(nextSection.getDownstation());
                currentSection.setDistance(currentSection.getDistance() + nextSection.getDistance());
                sections.remove(i + 1);
            }
        }
    }

}
