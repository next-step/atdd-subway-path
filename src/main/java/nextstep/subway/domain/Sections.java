package nextstep.subway.domain;

import nextstep.subway.exception.IllegalDeleteSectionException;
import nextstep.subway.exception.IllegalDistanceException;
import nextstep.subway.exception.NoMatchSectionException;
import nextstep.subway.exception.NotExistedStationDeleteException;

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

    private Long startStationId;

    public void initSection(Section section) {
        changeStartStationId(section.getUpStation().getId());
        sections.add(section);
    }

    public void addSection(Section section) {
        if (isAddUpSectionAtMiddle(section)) {
            addUpSectionAtMiddle(section);
            return;
        }
        if (isAddDownSectionAtMiddle(section)) {
            addDownSectionAtMiddle(section);
            return;
        }
        if (isAddSectionAtLeftest(section)) {
            changeStartStationId(section.getUpStation().getId());
            sections.add(section);
            return;
        }
        if (isAddSectionAtRightest(section)) {
            sections.add(section);
            return;
        }
        throw new NoMatchSectionException();
    }

    private boolean isAddDownSectionAtMiddle(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualDownStationName(section.getDownStation().getName()));
    }

    private boolean isAddSectionAtRightest(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualUpStationName(section.getDownStation().getName()));
    }

    private boolean isAddSectionAtLeftest(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualDownStationName(section.getUpStation().getName()));
    }

    private boolean isAddUpSectionAtMiddle(Section section) {
        return sections.stream().anyMatch(s -> s.isEqualUpStationName(section.getUpStation().getName()));
    }

    private void addUpSectionAtMiddle(Section section) {
        changeStartStationId(section.getDownStation().getId());
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

    private void addDownSectionAtMiddle(Section section) {
        changeStartStationId(section.getUpStation().getId());
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


    private void changeStartStationId(Long id) {
        this.startStationId = id;
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
        Long id = startStationId;
        List<Section> result = new ArrayList<>();
        result.add(sections.stream()
                .filter(section -> section.getUpStation().getId().equals(startStationId))
                .findFirst()
                .get());
        while (result.size() != sections.size()) {
            findNextUpStationId(id, result);
        }
        return result;
    }

    private Long findNextUpStationId(Long id, List<Section> result) {
        for (Section section : sections) {
            if (id.equals(section.getDownStation().getId())) {
                result.add(section);
                id = section.getUpStation().getId();
                return id;
            }
        }
        return null;
    }

    public boolean isDeleteUpMostSection(Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.isEqualUpStationId(stationId)) && !sections.stream()
                .anyMatch(section -> section.isEqualDownStationId(stationId));
    }

    public boolean isDeleteDownMostSection(Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.isEqualDownStationId(stationId)) && !sections.stream()
                .anyMatch(section -> section.isEqualUpStationId(stationId));
    }

    public boolean isDeleteMiddleSection(Long stationId) {
        return sections.stream()
                .anyMatch(section -> section.isEqualDownStationId(stationId)) && sections.stream()
                .anyMatch(section -> section.isEqualUpStationId(stationId));
    }

    public void deleteMiddleSection(Long stationId, Line line) {
        if(stationId == startStationId){
            changeStartStationId(getSections().stream()
                    .filter(section -> section.isEqualDownStationId(stationId))
                    .findFirst()
                    .orElseThrow(EntityNotFoundException::new)
                    .getUpStation().getId());
        }
        Section downSection = sections.stream().filter(section -> section.isEqualUpStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());

        Section upSection = sections.stream().filter(section -> section.isEqualDownStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());

        sections.remove(downSection);
        sections.remove(upSection);

        sections.add(new Section(line, downSection.getDownStation(), upSection.getUpStation(),
                downSection.getDistance() + upSection.getDistance()));
    }

    public void deleteUpMostSection(Long stationId) {
        Section rightSection = sections.stream().filter(section -> section.isEqualUpStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());
        sections.remove(rightSection);
    }

    public void deleteDownMostSection(Long stationId) {
        Section leftSection = sections.stream().filter(section -> section.isEqualDownStationId(stationId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException());
        startStationId = leftSection.getUpStation().getId();
        sections.remove(leftSection);
    }

    public void deleteSection(Long stationId, Line line) {
        if (isDeleteMiddleSection(stationId)) {
            deleteMiddleSection(stationId, line);
            return;
        }
        if (isDeleteUpMostSection(stationId)) {
            deleteUpMostSection(stationId);
            return;
        }
        if (isDeleteDownMostSection(stationId)) {
            deleteDownMostSection(stationId);
            return;
        }
        throw new NotExistedStationDeleteException();
    }
}