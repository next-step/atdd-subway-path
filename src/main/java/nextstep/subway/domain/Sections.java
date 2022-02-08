package nextstep.subway.domain;

import nextstep.subway.exception.IllegalDeleteSectionException;
import nextstep.subway.exception.IllegalDistanceException;
import nextstep.subway.exception.NoMatchSectionException;

import javax.persistence.CascadeType;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private static final int MINIMAL_DELETE_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    private String startStationName;

    public void initSection(Section section) {
        changeStartStationName(section.getUpStation().getName());
        sections.add(section);
    }

    public void addSection(Section section) {
        if (isAddMiddleAndRightSection(section)) {
            addMiddleAndRightSection(section);
            return;
        }
        if (isAddLeftAndMiddleSection(section)) {
            addLeftAndMiddleSection(section);
            return;
        }
        if (isLeftAddSection(section)) {
            changeStartStationName(section.getUpStation().getName());
            sections.add(section);
            return;
        }
        if (isRightAddSection(section)) {
            sections.add(section);
            return;
        }
        throw new NoMatchSectionException();
    }

    private boolean isAddLeftAndMiddleSection(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualDownStationName(section.getDownStation().getName()));
    }

    private boolean isRightAddSection(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualUpStationName(section.getDownStation().getName()));
    }

    private boolean isLeftAddSection(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualDownStationName(section.getUpStation().getName()));
    }

    private boolean isAddMiddleAndRightSection(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualUpStationName(section.getUpStation().getName()));
    }

    private void addMiddleAndRightSection(Section section) {
        changeStartStationName(section.getDownStation().getName());
        Section existedSection = sections.stream()
                .filter(s -> s.getUpStation().getName().equals(section.getUpStation().getName()))
                .findFirst().get();
        checkDistanceValidation(existedSection, section);
        checkNotSameSection(existedSection, section);
        sections.add(new Section(section.getLine(), section.getDownStation(), existedSection.getUpStation(),
                section.getDistance()));
        sections.add(new Section(section.getLine(), existedSection.getDownStation(), section.getDownStation(),
                existedSection.getDistance() - section.getDistance()));
        delete(existedSection);
    }

    private void addLeftAndMiddleSection(Section section) {
        changeStartStationName(section.getUpStation().getName());
        Section existedSection = sections.stream()
                .filter(s -> s.getDownStation().getName().equals(section.getDownStation().getName()))
                .findFirst().get();
        checkDistanceValidation(existedSection, section);
        checkNotSameSection(existedSection, section);
        sections.add(new Section(section.getLine(), section.getUpStation(), existedSection.getUpStation(),
                existedSection.getDistance() - section.getDistance()));
        sections.add(new Section(section.getLine(), existedSection.getDownStation(), section.getUpStation(),
                section.getDistance()));
        delete(existedSection);
    }

    private void checkDistanceValidation(Section existedSection, Section section) {
        if (existedSection.getDistance() <= section.getDistance()) {
            throw new IllegalDistanceException();
        }
    }

    private void checkNotSameSection(Section existedSection, Section section) {
        if (existedSection.getDownStation().getName().equals(section.getDownStation().getName()) &&
                existedSection.getUpStation().getName().equals(section.getUpStation().getName())) {
            throw new IllegalDistanceException();
        }
    }

    private void changeStartStationName(String name) {
        this.startStationName = name;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        sections.forEach(section -> {
            stations.add(section.getDownStation());
            stations.add(section.getUpStation());
        });
        Collections.sort(stations);
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public void delete(Section section) {
        if (sections.size() <= MINIMAL_DELETE_SIZE) {
            throw new IllegalDeleteSectionException();
        }
        sections.remove(section);
    }

    public void deleteLastSection() {
        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        if (sections.size() <= 1) {
            return sections;
        }
        String name = startStationName;
        List<Section> result = new ArrayList<>();
        result.add(sections.stream()
                .filter(section -> section.getUpStation().getName().equals(startStationName))
                .findFirst()
                .get());
        while (result.size() != sections.size()) {
            findNextUpStationName(name, result);
        }
        return result;
    }

    private String findNextUpStationName(String name, List<Section> result) {
        for (Section section : sections) {
            if (name.equals(section.getDownStation().getName())) {
                result.add(section);
                name = section.getUpStation().getName();
                return name;
            }
        }
        return null;
    }<<<<<<<HEAD

    public boolean isDeleteRightMostSection(Long stationId) {
        return sections.stream().anyMatch(section -> section.isEqualUpStationId(stationId))
                && !sections.stream().anyMatch(section -> section.isEqualDownStationId(stationId));
    }

    public boolean isDeleteLeftMostSection(Long stationId) {
        return sections.stream().anyMatch(section -> section.isEqualDownStationId(stationId))
                && !sections.stream().anyMatch(section -> section.isEqualUpStationId(stationId));
    }

    public boolean isDeleteMiddleSection(Long stationId) {
        return sections.stream().anyMatch(section -> section.isEqualDownStationId(stationId))
                && sections.stream().anyMatch(section -> section.isEqualUpStationId(stationId));
    }

    public void deleteMiddleSection(Long stationId, Line line) {
        Section leftSection = sections.stream().filter(section -> section.isEqualUpStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());
        Section rightSection = sections.stream().filter(section -> section.isEqualDownStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());
        sections.remove(leftSection);
        sections.remove(rightSection);
        sections.add(new Section(line, leftSection.getDownStation(), rightSection.getUpStation(),
                leftSection.getDistance() + rightSection.getDistance()));
    }

    public void deleteRightSection(Long stationId) {
        Section rightSection = sections.stream().filter(section -> section.isEqualUpStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());
        sections.remove(rightSection);
    }

    public void deleteLeftSection(Long stationId) {
        Section leftSection = sections.stream().filter(section -> section.isEqualDownStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());
        startStationName = leftSection.getUpStation().getName();
        sections.remove(leftSection);
    }
}=======}>>>>>>>0 a399a6929ae3ee65a23796c473f52560892c06e
