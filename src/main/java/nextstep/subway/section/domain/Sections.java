package nextstep.subway.section.domain;

import nextstep.subway.exception.*;
import nextstep.subway.section.service.SectionDto;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    private final static int FIRST = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            stations.add(section.getUpStation());
        }

        stations.add(getLastSection().getDownStation());

        return stations;
    }

    public List<Section> getSortedSections() {
//        return getSections().stream()
//                .sorted(Comparator.comparing((Section s) -> s.getUpStation().getName())
//                        .thenComparing((s1, s2) -> {
//                            String s1DownStationName = s1.getDownStation().getName();
//                            String s2UpStationName = s2.getUpStation().getName();
//                            return s1DownStationName.compareTo(s2UpStationName);
//                        }))
//                .collect(Collectors.toList());

        List<Section> sortedSections = sections;

        for (Section section : sections) {

            sortedSections.stream()
                    .filter(s ->

                    )

            ;

            // section



        }



        return null;
    }

    public void addSection(Section newSection) {
        validateAddSection(newSection);

        if (this.sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        if (isFirstSection(newSection)) {
            addFirstSection(newSection);
            return;
        }
        if (isLastSection(newSection)) {
            addLastSection(newSection);
            return;
        }
        addMiddleSection(newSection);
    }

    private void validateAddSection(Section newSection) {
        if (this.sections.isEmpty()) {
            return;
        }
        if (this.sections.contains(newSection)) {
            throw new AlreadyExistSectionException();
        }

        List<Station> stations = getStations();

        if (!stations.contains(newSection.getUpStation()) && !stations.contains(newSection.getDownStation())) {
            throw new NotFoundUpStationOrDownStation();
        }
    }

    private void addFirstSection(Section newSection) {
        if (!isFirstSection(newSection)) {
            return;
        }
        this.sections.add(newSection);
    }

    private void addLastSection(Section newSection) {
        if (!isLastSection(newSection)) {
            return;
        }
        this.sections.add(newSection);
    }

    private void addMiddleSection(Section newSection) {
        if (isFirstSection(newSection) && isLastSection(newSection)) {
            return;
        }

        Section section = sections.stream()
                .filter(s -> s.isUpStation(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new NotFoundStationException());

        section.updateUpStation(newSection.getDownStation());
        section.reduceDistance(newSection.getDistance());
        this.sections.add(newSection);
    }

    public void deleteSection(Station station) {
        if (!getStations().contains(station)) {
            throw new NotFoundStationException();
        }
        if (isNotLastStation(station)) {
            throw new IsNotLastStationException();
        }
        if (size() == 1) {
            throw new DeleteSectionException();
        }

        Section lastSection = getLastSection();

        lastSection.delete();
        this.sections.remove(lastSection);
    }

    private boolean isFirstSection(Section targetSection) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return getSortedSections().get(FIRST).getUpStation().equals(targetSection.getDownStation());
    }

    private boolean isLastSection(Section targetSection) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return getSortedSections().get(size() - 1).getDownStation().equals(targetSection.getUpStation());
    }

    private boolean isMiddleSection(Section targetSection) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }

        return sections.stream()
                .anyMatch(
                        section -> section.getUpStation().equals(targetSection.getUpStation())
                );
    }

    private boolean isNotLastStation(Station station) {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return !getLastSection().isDownStation(station);
    }

    private Section getLastSection() {
        if (this.sections.isEmpty()) {
            throw new EmptySectionException();
        }
        return this.sections.get(size() - 1);
    }

    public boolean hasSection(Section section) {
        return this.sections.contains(section);
    }

    public int size() {
        return this.sections.size();
    }

    public List<Section> getSections() {
        return this.sections;
    }
}
