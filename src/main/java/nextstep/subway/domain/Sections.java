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

    public Sections(Section section) {
        sections.add(section);
        firstStation = section.getUpStation();
        lastStation = section.getDownStation();
    }

    public void addSection(Section section) {
        validateAddSection(section);

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

        return sections.stream()
                .filter(sec -> sec.getUpStation().equals(firstStation))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public void removeSection(Station station) {
        sections.remove(sections.size() - 1);
    }

    public int getTotalDistance() {
        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    public boolean isNewFirstSection(Section section) {
        return section.getDownStation().equals(firstStation);
    }

    public boolean isNewLastSection(Section section) {
        return section.getUpStation().equals(lastStation);
    }

    private void validateAddSection(Section section) {
        List<Station> lineStations = getStations();
        if (lineStations.containsAll(section.stations())) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.ALREADY_EXIST);
        }

        if (!lineStations.contains(section.getUpStation()) && !lineStations.contains(section.getDownStation())) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.NOTHING_EXIST);
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

    private boolean isBetweenSection(Section origin, Section target) {
        return origin.getUpStation().equals(target.getUpStation())
                || origin.getDownStation().equals(target.getDownStation());
    }


}
