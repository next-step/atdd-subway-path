package nextstep.subway.domain;

import nextstep.subway.exception.AllIncludedStationException;
import nextstep.subway.exception.DistanceException;
import nextstep.subway.exception.NonIncludedStationException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return this.sortedSections();
    }

    public void add(Section section) {
        if (this.sections.isEmpty()) {
            this.sections.add(section);
            return;
        }

        final boolean isUpStationExists = existsStation(section.getUpStation());
        final boolean isDownStationExists = existsStation(section.getDownStation());

        // 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        if (isUpStationExists && isDownStationExists) {
            throw new AllIncludedStationException();
        }

        // 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
        if (!isUpStationExists && !isDownStationExists) {
            throw new NonIncludedStationException();
        }

        // 새로운 역을 상행/하행 종점으로 등록할 경우
        if (this.getFinalUpStation().equals(section.getDownStation()) || this.getFinalDownStation().equals(section.getUpStation())) {
            this.sections.add(section);
            return;
        }

        this.addStationBetweenExistsStations(section, isUpStationExists);
    }

    private void addStationBetweenExistsStations(Section section, boolean isUpStationExists) {
        final Section overlapSection = findOverlapSection(section, isUpStationExists);

        System.out.println(section.getDistance());
        System.out.println(overlapSection.getDistance());

        // 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
        if (section.getDistance() >= overlapSection.getDistance()) {
            throw new DistanceException();
        }

        // A-B 간선에 A-C 간선 추가 && A-B 간선에 C-B 간선 추가
        this.sections.add(section);
        overlapSection.setDistance(overlapSection.getDistance() - section.getDistance());
        if (isUpStationExists) {
            overlapSection.setUpStation(section.getDownStation());
        } else {
            overlapSection.setDownStation(section.getUpStation());
        }
    }

    public Section findOverlapSection(Section section, boolean isUpStation) {
        Predicate<Section> filter = isUpStation ? s -> s.getUpStation().equals(section.getUpStation()) : s -> s.getDownStation().equals(section.getDownStation());
        return this.sections.stream()
                .filter(filter)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean existsStation(Station station) {
        return this.getStations().stream().anyMatch(s -> s.equals(station));
    }

    public void deleteSection(Station station) {
        if (!this.getFinalDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        this.getSections().get(this.sections.size() - 1).delete();
    }

    private Station getFinalUpStation() {
        List<Station> upStations = this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        return upStations.stream().filter(s -> !downStations.contains(s)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    private Station getFinalDownStation() {
        List<Station> upStations = this.sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        List<Station> downStations = this.sections.stream().map(Section::getDownStation).collect(Collectors.toList());

        return downStations.stream().filter(s -> !upStations.contains(s)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    private List<Section> sortedSections() {
        List<Section> sortedSections = new ArrayList<>();
        final Station finalUpStation = getFinalUpStation();
        Section section = this.sections.stream().filter(s -> s.getUpStation().equals(finalUpStation)).findAny().orElseThrow(IllegalArgumentException::new);
        sortedSections.add(section);

        for (int i = 0; i < this.sections.size()-1; i++) {
            final Station downStation = section.getDownStation();
            section = this.sections.stream().filter(s -> s.getUpStation().equals(downStation)).findAny().orElseThrow(IllegalArgumentException::new);
            sortedSections.add(section);
        }

        sortedSections.forEach(s -> System.out.println(s.getUpStation() + "->" + s.getDownStation()));
        return sortedSections;
    }
}
