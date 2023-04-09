package nextstep.subway.domain;

import nextstep.subway.exception.AlreadyRegisterBothStationException;
import nextstep.subway.exception.NeitherRegisterStationException;
import nextstep.subway.exception.NotLastStationException;
import nextstep.subway.exception.SingleSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        checkSection(section);
        addToMiddle(section);
        addToFirstOrLast(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        sections.forEach(x -> stations.add(x.getDownStation()));
        return stations;
    }

    public void remove(Station station) {
        if (!isLastStation(station)) {
            throw new NotLastStationException();
        }
        if (isSingleSection()) {
            throw new SingleSectionException();
        }
        sections.remove(getLastStationIndex());
    }

    private void checkSection(Section section) {
        if (isUpStation(section) && isDownStation(section)) {
            throw new AlreadyRegisterBothStationException();
        }
        if (!(containsStation(section.getUpStation()) || containsStation(section.getDownStation()))) {
            throw new NeitherRegisterStationException();
        }
    }

    private void addToFirstOrLast(Section section) {
        if (isLastStation(section.getUpStation())) {
            sections.add(section);
        }
        if (isFirstStation(section.getDownStation())) {
            sections.add(0, section);
        }
    }

    private void addToMiddle(Section section) {
        if (isUpStation(section)) {
            addContainsUpStationInSections(section);
        }
        if (isDownStation(section)) {
            addContainsDownStationInSections(section);
        }
    }

    private void addContainsUpStationInSections(Section section) {
        int index = getSectionIndex(section);
        sections.stream()
                .filter(it -> it.existsUpStation(section.getUpStation()) && !it.existsDownStation(section.getDownStation()))
                .findFirst()
                .ifPresent(it -> {
                    sections.add(index, section);
                    sections.add(index + 1, new Section(section.getLine(), section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()));
                    sections.remove(it);
                });
    }

    private void addContainsDownStationInSections(Section section) {
        int index = getSectionIndex(section);
        sections.stream()
                .filter(it -> it.existsDownStation(section.getDownStation()) && !it.existsUpStation(section.getUpStation()))
                .findFirst()
                .ifPresent(it -> {
                    sections.add(index, new Section(section.getLine(), it.getUpStation(), section.getUpStation(), it.getDistance() - section.getDistance()));
                    sections.add(index + 1, section);
                    sections.remove(it);
                });
    }

    boolean isUpStation(Section section) {
        return sections.stream()
                .anyMatch(it -> it.existsUpStation(section.getUpStation()) && !it.existsDownStation(section.getDownStation()));
    }

    boolean isDownStation(Section section) {
        return sections.stream()
                .anyMatch(it -> it.existsDownStation(section.getDownStation()) && !it.existsUpStation(section.getUpStation()));
    }

    public boolean isFirstStation(Station station) {
        return sections.get(0).getUpStation().equals(station);
    }

    public boolean isLastStation(Station station) {
        return getLastStation().equals(station);
    }

    private Station getLastStation() {
        return sections.get(getLastStationIndex()).getDownStation();
    }

    private int getSectionIndex(Section section) {
        return IntStream
                .range(0, sections.size())
                .filter(i -> Objects.equals(sections.get(i), section))
                .findFirst()
                .orElse(0);
    }

    private boolean containsStation(Station station) {
        return getStations().contains(station);
    }

    public boolean isSingleSection() {
        return sections.size() == 1;
    }

    private int getLastStationIndex() {
        return sections.size() - 1;
    }

    public int calcDistance() {
        return sections.stream()
                .mapToInt(Section::getDistance)
                .sum();
    }

    public List<Section> getSections() {
        return sections;
    }
}
