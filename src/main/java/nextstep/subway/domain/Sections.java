package nextstep.subway.domain;

import nextstep.subway.exception.IllegalDeleteSectionException;
import nextstep.subway.exception.IllegalDistanceException;
import nextstep.subway.exception.IllegalSectionException;
import nextstep.subway.exception.NoMatchSectionException;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private static final int MINIMAL_DELETE_SIZE = 1;
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void init(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        if (isMiddleAddSection(section)) {
            addMiddleSection(section);
            return;
        }
        if (isLeftAddSection(section)) {
            sections.add(section);
            return;
        }
        if (isRightAddSection(section)) {
            sections.add(section);
            return;
        }
        throw new NoMatchSectionException();
    }

    private boolean isRightAddSection(Section section) {
        return sections.stream().filter(s -> s.getUpStation().getName().equals(section.getDownStation().getName()))
                .findFirst()
                .isPresent();
    }

    private boolean isLeftAddSection(Section section) {
        return sections.stream().filter(s -> s.getDownStation().getName().equals(section.getUpStation().getName()))
                .findFirst()
                .isPresent();
    }

    private boolean isMiddleAddSection(Section section) {
        return sections.stream().filter(s -> s.getUpStation().getName().equals(section.getUpStation().getName()))
                .findFirst()
                .isPresent() || sections.stream().filter(s -> s.getDownStation().getName().equals(section.getDownStation().getName()))
                .findFirst()
                .isPresent();
    }

    private void addMiddleSection(Section section) {
        if (sections.stream().filter(s -> s.getUpStation().getName().equals(section.getUpStation().getName()))
                .findFirst()
                .isPresent()) {
            Section existedSection = sections.stream()
                    .filter(s -> s.getUpStation().getName().equals(section.getUpStation().getName()))
                    .findFirst().get();
            if (existedSection.getDistance() <= section.getDistance()) {
                throw new IllegalDistanceException();
            }
            if (section.getDownStation().getName().equals(existedSection.getDownStation().getName())) {
                throw new IllegalSectionException();
            }
            sections.add(new Section(section.getDownStation(), existedSection.getUpStation(), section.getDistance()));
            sections.add(new Section(existedSection.getDownStation(), section.getDownStation(), existedSection.getDistance() - section.getDistance()));
            delete(sections.stream().filter(s -> s.getUpStation().getName().equals(section.getUpStation().getName()))
                    .findFirst().get());
        }
        if (sections.stream().filter(s -> s.getDownStation().getName().equals(section.getDownStation().getName()))
                .findFirst()
                .isPresent()) {
            Section existedSection = sections.stream()
                    .filter(s -> s.getDownStation().getName().equals(section.getDownStation().getName()))
                    .findFirst().get();
            if (existedSection.getDistance() <= section.getDistance()) {
                throw new IllegalDistanceException();
            }
            if (section.getUpStation().getName().equals(existedSection.getUpStation().getName())) {
                throw new IllegalSectionException();
            }
            sections.add(new Section(section.getLine(), section.getUpStation(), existedSection.getUpStation(), existedSection.getDistance() - section.getDistance()));
            sections.add(new Section(section.getLine(), existedSection.getDownStation(), section.getUpStation(), section.getDistance()));
            delete(sections.stream().filter(s -> s.getDownStation().getName().equals(section.getDownStation().getName()))
                    .findFirst().get());
        }
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
        Collections.sort(sections);
        return sections;
    }
}
