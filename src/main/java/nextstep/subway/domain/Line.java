package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    private Long finalUpStationId;
    private Long finalDownStationId;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color, Long finalUpStationId, Long finalDownStationId) {
        this.name = name;
        this.color = color;
        this.finalUpStationId = finalUpStationId;
        this.finalDownStationId = finalDownStationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void deleteSection(Station station) {
        validateSectionDeleteRequest(station);

        List<Section> filteredSections = sections.stream()
                .filter(
                        s -> s.checkBelongingStationId(station.getId())
                ).collect(Collectors.toList());

        //중간에 위치한 경우
        if (deleteMiddlePositionSection(station, filteredSections)) {
            return;
        }

        //상행 종착 구간에 위치한 경우
        deleteLastPositionSection(station, filteredSections);
    }

    private void deleteLastPositionSection(Station station, List<Section> filteredSections) {
        if (filteredSections.get(0).getUpStationId() == station.getId()) {
            updateFinalUpStationId(filteredSections.get(0).getDownStationId());
        } else {
            // 하행 종착 구간에 위치한 경우
            updateFinalDownStationId(filteredSections.get(0).getUpStationId());
        }

        sections.remove(filteredSections.get(0));
    }

    private boolean deleteMiddlePositionSection(Station station, List<Section> filteredSections) {
        if(filteredSections.size() > 1) {
            Section first, second;

            if(filteredSections.get(0).getUpStationId() == station.getId()) {
                first = filteredSections.get(1);
                second = filteredSections.get(0);
            } else {
                first = filteredSections.get(0);
                second = filteredSections.get(1);
            }

            first.updateDownStation(second.getDownStation());
            first.updateDistance(first.getDistance() + second.getDistance());

            sections.remove(second);

            return true;
        }
        return false;
    }

    public Long getFinalUpStationId() {
        return finalUpStationId;
    }

    public Long getFinalDownStationId() {
        return finalDownStationId;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
        section.updateLine(this);
    }

    public void updateFinalUpStationId(Long finalUpStationId) {
        this.finalUpStationId = finalUpStationId;
    }

    public void updateFinalDownStationId(Long finalDownStationId) {
        this.finalDownStationId = finalDownStationId;
    }

    public void validateAddSectionConditions(Long newUpStationId, Long newDownStationId) {
        Set<Long> stationIds = getSectionContainStationsSet();

        if (stationIds.contains(newUpStationId) && stationIds.contains(newDownStationId)) {
            throw new IllegalArgumentException();
        }

        if (!stationIds.contains(newUpStationId) && !stationIds.contains(newDownStationId)) {
            throw new IllegalArgumentException();
        }
    }

    public void validateSectionDeleteRequest(Station targetStation) {
        if (sections.size() <= 1) {
            throw new UnsupportedOperationException();
        }

        Set<Long> sectionContainStationsSet = getSectionContainStationsSet();
        if (!sectionContainStationsSet.contains(targetStation.getId())) {
            throw new IllegalArgumentException();
        }
    }

    private Set<Long> getSectionContainStationsSet() {
        List<Long> sectionUpStationIds = sections.stream()
                .map(s -> s.getUpStationId())
                .collect(Collectors.toList());
        List<Long> sectionDownStationIds = sections.stream()
                .map(s -> s.getDownStationId())
                .collect(Collectors.toList());

        return Stream.of(sectionUpStationIds, sectionDownStationIds)
                .flatMap(x -> x.stream())
                .collect(Collectors.toSet());
    }
}
