package nextstep.subway.section;


import lombok.extern.slf4j.Slf4j;
import nextstep.subway.exception.InvalidInputException;
import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Embeddable
@Slf4j
public class Sections implements Iterable<Section> {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @OrderColumn(name = "sections_sequence")
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

    public int getTotalDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public void initSection(Section section) {
        sections.add(section);
    }

    public boolean isFirstUpstation(Station station) {
        return getFirstUpstation().equals(station);
    }

    public boolean isLastDownstation(Station station) {
        return getLastDownstation().equals(station);
    }

    public Station getFirstUpstation() {
        return sections.get(0).getUpstation();
    }

    public Station getLastDownstation() {
        return sections.get(sections.size() - 1).getDownstation();
    }

    public Section getNextSection(Section currentSection) {
        boolean next = false;
        for (Section section : sections) {
            if (next) {
                return section;
            }
            if (section.equals(currentSection)) {
                next = true;
            }
        }
        return null;
    }

    public void addFirstSection(Section newSection) {
        if (sections.stream().anyMatch(section ->
                section.isInSection(newSection))) {
            throw new InvalidInputException("새로운 구간의 하행역은 이미 노선에 존재하는 역이면 안 됩니다.");
        }

        sections.add(0, newSection);
    }

    public void addSection(Section newSection) {
        // newSection의 upstation, downstation 둘 다 노선에 등록되어있는 거면 안 됨
        boolean upstationExists = sections.stream().anyMatch(section ->
                section.isUpstation(newSection.getUpstation()) ||
                section.isDownstation(newSection.getUpstation()));
        boolean downstationExists = sections.stream().anyMatch(section ->
                section.isUpstation(newSection.getDownstation()) ||
                section.isDownstation(newSection.getDownstation()));

        if (upstationExists && downstationExists) {
            throw new InvalidInputException("새로운 구간의 상행역과 하행역 둘 다 이미 노선에 등록되어 있습니다.");
        }

        if (!upstationExists && !downstationExists) {
            throw new InvalidInputException("새로운 구간은 기존 노선의 역과 최소 하나 이상 연결되어야 합니다.");
        }

        if (upstationExists) {
            int size = sections.size();
            for (int i = 0; i < size; i++) {
                Section currentSection = sections.get(i);
                if (currentSection.isUpstation(newSection.getUpstation())) {
                    int newDistance = currentSection.getDistance() - newSection.getDistance();
                    if (newDistance <= 0) {
                        throw new InvalidInputException("유효하지 않은 거리입니다.");
                    }

                    currentSection.setUpstation(newSection.getDownstation());
                    currentSection.setDistance(newDistance);
                    sections.add(i, newSection);
                    return;
                }
            }
        } else {
            int size = sections.size();
            for (int i = 0; i < size; i++) {
                Section currentSection = sections.get(i);
                if (currentSection.isDownstation(newSection.getDownstation())) {
                    int newDistance = currentSection.getDistance() - newSection.getDistance();
                    if (newDistance <= 0) {
                        throw new InvalidInputException("유효하지 않은 거리입니다.");
                    }

                    currentSection.setDownstation(newSection.getUpstation());
                    currentSection.setDistance(newDistance);
                    sections.add(i + 1, newSection);
                    return;
                }
            }
        }

    }

    public void addLastSection(Section newSection) {
        if (sections.stream().anyMatch(section ->
                section.isInSection(newSection))) {
            throw new InvalidInputException("새로운 구간의 하행역은 이미 노선에 존재하는 역이면 안 됩니다.");
        }

        sections.add(newSection);
    }

    public Section removeFirstSection() {
        Section removedSection = sections.remove(0);
        removedSection.setLine(null);
        return removedSection;
    }

    public Section removeLastSection() {
        Section removedSection = sections.remove(sections.size() - 1);
        removedSection.setLine(null);
        return removedSection;
    }

    public Section removeSection(Station station) {
        int size = sections.size();
        for (int i = 0; i < size; i++) {
            Section currentSection = sections.get(i);
            if (currentSection.isDownstation(station)) {

                Section nextSection = sections.get(i + 1);
                currentSection.setDownstation(nextSection.getDownstation());
                currentSection.setDistance(currentSection.getDistance() + nextSection.getDistance());
                Section removedSection = sections.remove(i + 1);
                removedSection.setLine(null);

                return removedSection;
            }
        }
        return null;
    }

}
