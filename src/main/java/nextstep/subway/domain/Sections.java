package nextstep.subway.domain;

import nextstep.subway.domain.exception.SectionExceptionMessages;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(Section section) {
        this.sections.add(section);
    }

    public void addSection(Line line, Section section) {
        validateAddSection(line, section);

        if (!hasSections()) {
            sections.add(section);
            line.changeFirstStation(section.getUpStation());
            line.changeLastStation(section.getDownStation());
            return;
        }

        if (isNewFirstSection(line, section)) {
            sections.add(section);
            line.changeFirstStation(section.getUpStation());
            return;
        }

        if (isNewLastSection(line, section)) {
            sections.add(section);
            line.changeLastStation(section.getDownStation());
            return;
        }

        addBetweenSection(line, section);
    }

    public boolean hasSections() {
        return sections.size() > 0;
    }

    public int getSectionsCount() {
        return sections.size();
    }

    public Section getFirstSection(Line line) {
        if (!hasSections()) {
            throw new NoSuchElementException();
        }

        if (getSectionsCount() == 1) {
            return sections.get(0);
        }

        return sections.stream()
                .filter(sec -> isFirstSection(line, sec))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }



    public Section getNextSection(Section currSection) {
        return sections.stream()
                .filter(sec -> sec.getUpStation().equals(currSection.getDownStation()))
                .findFirst().orElse(null);
    }

    public void removeSection(Station removeStation, Station lastStation) {
        if (!removeStation.equals(lastStation)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public int getTotalDistance() {
        if (!hasSections()) {
            return 0;
        }

        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    private boolean isFirstSection(Line line, Section section) {
        return section.getUpStation().equals(line.getFirstStation());
    }

    private boolean isNewFirstSection(Line line, Section section) {
        return section.getDownStation().equals(line.getFirstStation());
    }

    private boolean isNewLastSection(Line line, Section section) {
        return section.getUpStation().equals(line.getLastStation());
    }

    private void validateAddSection(Line line, Section section) {
        if (!hasSections()) {
            return;
        }

        List<Station> lineStations = line.getStations();
        if (lineStations.containsAll(section.stations())) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.ALREADY_EXIST);
        }

        if (!lineStations.contains(section.getUpStation()) && !lineStations.contains(section.getDownStation())) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.NOTHING_EXIST);
        }
    }

    private void addBetweenSection(Line line, Section section) {
        Section originSection =  sections.stream()
                .filter(s -> isBetweenSection(s, section))
                .findFirst().orElseThrow(NoSuchElementException::new);

        if (section.getDistance() >= originSection.getDistance()) {
            throw new DataIntegrityViolationException(SectionExceptionMessages.INVALID_DISTANCE);
        }

        removeSection(originSection.getDownStation(), line.getLastStation());

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
