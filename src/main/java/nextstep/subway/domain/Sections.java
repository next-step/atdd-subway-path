package nextstep.subway.domain;

import nextstep.subway.domain.exception.SectionExceptionMessages;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Station firstStation;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Station lastStation;

    public Sections() {

    }

    public void addSection(Section section) {
        validateAddSection(section);

        if (sections.isEmpty()) {
            sections.add(section);
            firstStation = section.getUpStation();
            lastStation = section.getDownStation();
            return;
        }

        if (isNewFirstSection(section)) {
            sections.add(section);
            firstStation = section.getUpStation();
            return;
        }

        if (isNewLastSection(section)) {
            sections.add(section);
            lastStation = section.getDownStation();
            return;
        }

        addBetweenSection(section);
    }

    public int getSectionsCount() {
        return sections.size();
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

        return getStationIncludeSection(firstStation);
    }

    private Section getStationIncludeSection(Station station) {
        return sections.stream()
                .filter(sec -> sec.getUpStation().equals(station) || sec.getDownStation().equals(station))
                .findFirst().orElseThrow(() -> new DataIntegrityViolationException(SectionExceptionMessages.CANNOT_FIND_SECTION));
    }

    public void removeSection(Station station) {
        validateRemoveSection();

        if (station.equals(firstStation)) {
            Section section = getStationIncludeSection(firstStation);
            firstStation = section.getDownStation();
            sections.remove(section);
            return;
        }

        if (station.equals(lastStation)) {
            Section section = getStationIncludeSection(lastStation);
            lastStation = section.getUpStation();
            sections.remove(section);
            return;
        }

        removeBetweenSection(station);
    }

    public int getTotalDistance() {
        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    private boolean isNewFirstSection(Section section) {
        return section.getDownStation().equals(firstStation);
    }

    private boolean isNewLastSection(Section section) {
        return section.getUpStation().equals(lastStation);
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

    private void addBetweenSection(Section section) {
        Section originSection =  sections.stream()
                .filter(s -> isBetweenSection(s, section))
                .findFirst().orElseThrow(NoSuchElementException::new);

        if (section.getDistance() >= originSection.getDistance()) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.INVALID_DISTANCE);
        }

        removeSection(originSection.getDownStation());

        sections.add(section);

        int newDistance = originSection.getDistance() - section.getDistance();
        if (originSection.getDownStation().equals(section.getDownStation())) {
            sections.add(new Section(originSection.getLine(), originSection.getUpStation(), section.getUpStation(), newDistance));
            return;
        }

        if (originSection.getUpStation().equals(section.getUpStation())) {
            sections.add(new Section(originSection.getLine(), section.getDownStation(), originSection.getDownStation(), newDistance));
            return;
        }
    }

    private void removeBetweenSection(Station station) {
        Section section = getStationIncludeSection(station);
        Section nextSection = getNextSection(section);
        sections.remove(section);
        sections.remove(nextSection);

        int newDistance = section.getDistance()+nextSection.getDistance();
        addSection(new Section(section.getLine(), section.getUpStation(), nextSection.getDownStation(), newDistance));
    }

    private boolean isBetweenSection(Section origin, Section target) {
        return origin.getUpStation().equals(target.getUpStation())
                || origin.getDownStation().equals(target.getDownStation());
    }


}
