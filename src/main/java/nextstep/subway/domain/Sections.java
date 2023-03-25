package nextstep.subway.domain;

import nextstep.subway.exception.NotLastStationException;
import nextstep.subway.exception.SingleSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;
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
        addContainsUpStationInSections(section);
        addContainsDownStationInSections(section);

        if (isFirstStation(section.getDownStation())) {
            sections.add(0, section);
            return;
        }

        if (isLastStation(section.getUpStation())) {
            sections.add(section);
        }
    }

    public List<Station> getStations() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        stations.addAll(sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList()));
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

    private boolean isEmpty() {
        return this.sections.isEmpty();
    }

    private void checkSection(Section section) {
        if (containsUpStation(section).isPresent() && containsDownStation(section).isPresent()) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
        if (!containsStation(section.getUpStation()) && !containsStation(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
        }
    }

    private void addContainsUpStationInSections(Section section) {
        int index = getSectionIndex(section);
        containsUpStation(section)
                .ifPresent(it -> {
                    validateDistanceInMiddleSection(it, section);
                    sections.add(index, section);
                    sections.add(index + 1, new Section(section.getLine(), section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()));
                    sections.remove(it);
                });
    }

    private void addContainsDownStationInSections(Section section) {
        int index = getSectionIndex(section);
        containsDownStation(section)
                .ifPresent(it -> {
                    sections.add(index, new Section(section.getLine(), it.getUpStation(), section.getUpStation(), it.getDistance() - section.getDistance()));
                    sections.add(index + 1, section);
                    validateDistanceInMiddleSection(it, section);
                    sections.remove(it);
                });
    }

    private Optional<Section> containsUpStation(Section section) {
        return sections.stream()
                .filter(it -> it.existsUpStation(section.getUpStation()) && !it.existsDownStation(section.getDownStation()))
                .findFirst();
    }

    private Optional<Section> containsDownStation(Section section) {
        return sections.stream()
                .filter(it -> it.existsDownStation(section.getDownStation()) && !it.existsUpStation(section.getUpStation()))
                .findFirst();
    }

    private void validateDistanceInMiddleSection(Section it, Section section) {
        if (it.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
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
