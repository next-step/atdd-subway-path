package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int FIRST_SECTION_INDEX = 0;
    private static final String ALL_MATCH_SECTION_ERROR_MESSAGE = "상행역과 하행역이 모두 등록된 상태입니다.";
    private static final String NONE_MATCH_SECTION_ERROR_MESSAGE = "상행역과 하행역이 모두 등록되지 않았습니다.";


    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() { }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        boolean isUpStation = anyMatchUpStation(newSection);
        boolean isDownStation = anyMatchDownStation(newSection);

        validationSection(isUpStation, isDownStation);

        if (isUpStation) {
            addUpSection(newSection);
            return;
        }

        if (isDownStation) {
            addDownSection(newSection);
            return;
        }

        sections.add(newSection);
    }

    private void addUpSection(Section newSection) {
        int index = findUpSection(newSection);
        Section section = sections.get(index);

        if (index == FIRST_SECTION_INDEX
            && section.isUpStation(newSection.getDownStation())) {
            sections.add(index, newSection);
            return;
        }

        section.changeUpStationToNewDownStations(newSection);
        sections.add(index, newSection);
    }

    private void addDownSection(Section newSection) {
        int index = findDownSection(newSection);
        Section section = sections.get(index);

        if (index == (sections.size() - 1)
                && section.isDownStation(newSection.getUpStation())) {
            sections.add(newSection);
            return;
        }

        section.changeDownStationToNewUpStations(newSection);
        sections.add(index, newSection);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Comparator<Section> sortSection = new Comparator<Section>() {
            @Override
            public int compare(Section o1, Section o2) {
                return o2.getUpStation().equals(o1.getDownStation()) ? -1 : 0;
            }
        };

        Collections.sort(sections, sortSection);

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(FIRST_SECTION_INDEX, sections.get(FIRST_SECTION_INDEX).getUpStation());

        return Collections.unmodifiableList(stations);
    }

    public void deleteSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    private void validationSection(boolean isUpStation, boolean isDownStation) {
        if (sections.isEmpty()) {
            return;
        }

        if (isUpStation && isDownStation) {
            throw new IllegalArgumentException(ALL_MATCH_SECTION_ERROR_MESSAGE);
        }

        if (!isUpStation && !isDownStation) {
            throw new IllegalArgumentException(NONE_MATCH_SECTION_ERROR_MESSAGE);
        }
    }

    private int findUpSection(Section newSection) {
        Section section = sections.stream()
                .filter(sec -> sec.matchUpStation(newSection))
                .findFirst()
                .orElse(new Section());

        return sections.indexOf(section);
    }

    private int findDownSection(Section newSection) {
        Section section = sections.stream()
                .filter(sec -> sec.matchDownStation(newSection))
                .findFirst()
                .orElse(new Section());

        return sections.indexOf(section);
    }

    private boolean anyMatchUpStation(Section newSection) {
        return sections.stream()
                .filter(sec -> sec.matchUpStation(newSection))
                .count() > 0;
    }

    private boolean anyMatchDownStation(Section newSection) {
        return sections.stream()
                .filter(sec -> sec.matchDownStation(newSection))
                .count() > 0;
    }
}
