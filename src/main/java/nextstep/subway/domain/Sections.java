package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotFoundException;
import nextstep.subway.domain.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        if (isFirstOrEndpoint(section)) {
            sections.add(section);
            return;
        }

        validate(section);
        addBetweenSection(section);
        sections.add(section);
    }

    private boolean isFirstOrEndpoint(Section section) {
        List<Station> stations = sortedStations();
        if (sections.isEmpty()) {
            return true;
        }

        Station upEndStation = stations.get(0);
        Station downEndStation = stations.get(stations.size() - 1);
        return upEndStation == section.getDownStation() ||
                downEndStation == section.getUpStation();
    }

    private void validate(Section section) {
        List<Station> stations = sortedStations();
        if (containAllStations(stations, section)) {
            throw new SubwayException("상행역과 하행역이 이미 노선에 등록되어 있습니다.");
        }

        if (isNotContainedAnyStations(stations, section)) {
            throw new SubwayException("노선에 등록되지 않은 상행역 또는 하행역이 포함되어 있습니다.");
        }
    }

    private boolean containAllStations(List<Station> stations, Section section) {
        return stations.contains(section.getUpStation()) &&
                stations.contains(section.getDownStation());

    }

    private boolean isNotContainedAnyStations(List<Station> stations, Section section) {
        return !stations.contains(section.getUpStation()) &&
                !stations.contains(section.getDownStation());
    }

    private void addBetweenSection(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.isSameUpStation(section) ||
                        oldSection.isSameDownStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(oldSection.to(section));
                    sections.remove(oldSection);
                });
    }

    public List<Station> sortedStations() {
        List<Section> sections = sortedSections();
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedList<Station> stations = new LinkedList<>();
        sections.forEach(section -> stations.add(section.getDownStation()));
        stations.addFirst(sections.get(0).getUpStation());
        return stations;
    }

    public void remove(long stationId) {
        if (sections.size() <= 1) {
            throw new SubwayException("구간이 1개 이하인 경우, 삭제할 수 없습니다.");
        }

        List<Section> deleteSections = findSectionsByStationId(stationId);
        if (deleteSections.size() == 2) {
            Section mergedSection = Section.merge(deleteSections.get(0), deleteSections.get(1));
            sections.add(mergedSection);
        }

        sections.removeAll(deleteSections);
    }
    private List<Section> findSectionsByStationId(long stationId) {
        List<Section> deleteSections = sortedSections().stream()
            .filter(it -> it.hasStationId(stationId))
            .collect(Collectors.toList());

        if (deleteSections.isEmpty()) {
            throw new NotFoundException(stationId + "번 역을 찾을 수 없습니다.");
        }

        return deleteSections;
    }

    public List<Section> sortedSections() {
        List<Section> sortedSections = new ArrayList<>();
        findUpEndSection().ifPresent(upEndSection -> {
            Section nextSection = upEndSection;
            while (nextSection != null) {
                sortedSections.add(nextSection);
                nextSection = nextOf(nextSection);
            }
        });

        return sortedSections;
    }

    private Optional<Section> findUpEndSection() {
        List<Station> downStations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return sections.stream()
            .filter(section -> !downStations.contains(section.getUpStation()))
            .findFirst();
    }

    private Section nextOf(Section section) {
        return sections.stream()
            .filter(it -> it.getUpStation() == section.getDownStation())
            .findFirst()
            .orElse(null);
    }
}
