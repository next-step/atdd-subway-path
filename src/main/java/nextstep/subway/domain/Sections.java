package nextstep.subway.domain;

import nextstep.subway.domain.exception.SectionExceptionMessages;
import nextstep.subway.domain.policy.AddSectionPolicy;
import nextstep.subway.domain.policy.AddSectionPolicyFactory;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {

    }

    public void addSection(Section section) {
        validateAddSection(section);

        AddSectionPolicy policy = AddSectionPolicyFactory.of(this, sections, section);
        policy.execute();
    }

    public int getSectionsCount() {
        return sections.size();
    }

    public boolean isEmpty(){
        return sections.isEmpty();
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();

        Section currSection = getFirstSection();
        while (currSection != null) {
            stations.add(currSection.getUpStation());
            stations.add(currSection.getDownStation());
            currSection = getNextSection(currSection);
        }

        return new ArrayList<>(stations);
    }

    public Section getFirstSection() {
        if (getSectionsCount() == 1) {
            return sections.get(0);
        }

        return getStationIncludeSection(getFirstStation());
    }

    public Station getFirstStation() {
        Set<Station> allDownStations = sections.stream().map(Section::getDownStation).collect(Collectors.toSet());

        return sections.stream()
                .filter(sec -> !allDownStations.contains(sec.getUpStation()))
                .findAny()
                .map(Section::getUpStation)
                .orElseThrow(NoSuchElementException::new);
    }

    public Station getLastStation() {
        Set<Station> allUpStations = sections.stream().map(Section::getUpStation).collect(Collectors.toSet());

        return sections.stream()
                .filter(sec -> !allUpStations.contains(sec.getDownStation()))
                .findAny()
                .map(Section::getDownStation)
                .orElseThrow(NoSuchElementException::new);
    }

    private Section getStationIncludeSection(Station station) {
        return sections.stream()
                .filter(sec -> sec.getUpStation().equals(station) || sec.getDownStation().equals(station))
                .findFirst().orElseThrow(() -> new DataIntegrityViolationException(SectionExceptionMessages.CANNOT_FIND_SECTION));
    }

    public void removeSection(Station station) {
        validateRemoveSection();

        if (station.equals(getFirstStation())) {
            Section section = getStationIncludeSection(getFirstStation());
            sections.remove(section);
            return;
        }

        if (station.equals(getLastStation())) {
            Section section = getStationIncludeSection(getLastStation());
            sections.remove(section);
            return;
        }

        removeBetweenSection(station);
    }

    public int getTotalDistance() {
        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    public List<Section> getAllSections() {
        return Collections.unmodifiableList(sections);
    }

    private void validateAddSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        List<Station> lineStations = getStations();
        if (lineStations.containsAll(section.stations())) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.ALREADY_EXIST);
        }

        if (!lineStations.contains(section.getUpStation()) && !lineStations.contains(section.getDownStation())) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.NOTHING_EXIST);
        }
    }

    private void validateRemoveSection() {
        if (getSectionsCount() == 1) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.CANNOT_REMOVE_SECTION_WHEN_LINE_HAS_ONLY_ONE);
        }
    }

    private Section getNextSection(Section currSection) {
        return sections.stream()
                .filter(sec -> sec.getUpStation().equals(currSection.getDownStation()))
                .findFirst().orElse(null);
    }

    private void removeBetweenSection(Station station) {
        Section section = getStationIncludeSection(station);
        Section nextSection = getNextSection(section);
        sections.remove(section);
        sections.remove(nextSection);

        addSection(section.merge(nextSection));
    }

}
