package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (sections.size() == 0) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }
        checkSectionAddValidity(line, upStation, downStation);

        if (newUpDownStationIsLastStation(line, upStation, downStation)) {
            sections.add(new Section(line, upStation, downStation, distance));
            return;
        }
        addNewSectionsFromOldSection(line, upStation, downStation, distance);
    }

    private void checkSectionAddValidity(Line line, Station upStation, Station downStation) {
        if (line.isBothStationsNotIncluded(upStation, downStation)) {
            throw new RuntimeException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
        if (line.isBothStationsAlreadyIncluded(upStation, downStation)) {
            throw new RuntimeException("상행역과 하행역이 모두 노선에 등록되어 있다면 추가할 수 없습니다.");
        }
    }

    private boolean newUpDownStationIsLastStation(Line line, Station upStation, Station downStation) {
        if (line.getStations().get(0) == downStation) {
            return true;
        }
        if (line.getStations().get(line.getStations().size() - 1) == upStation) {
            return true;
        }
        return false;
    }

    private void addNewSectionsFromOldSection(Line line, Station upStation, Station downStation, int distance) {
        Section oldSection = findSection(upStation, downStation);
        int oldDistance = oldSection.getDistance();
        checkSectionDistanceValidity(oldDistance, distance);


        if (oldSection.getUpStation() == upStation) {
            sections.add(new Section(line, upStation, downStation, distance));
            sections.add(new Section(line, downStation, oldSection.getDownStation(), oldDistance - distance));
            line.removeSection(oldSection.getDownStation());
            return;
        }

        sections.add(new Section(line, oldSection.getUpStation(), upStation, oldDistance - distance));
        sections.add(new Section(line, upStation, downStation, distance));
        line.removeSection(oldSection.getDownStation());

    }

    private Section findSection(Station upStation, Station downStation) {
        Section sectionWithUpStation = sections.stream().filter(it -> it.getUpStation() == upStation).findFirst().orElse(null);
        Section sectionWithDownStation = sections.stream().filter(it -> it.getDownStation() == downStation).findFirst().orElse(null);
        if (sectionWithUpStation == null) {
            return sectionWithDownStation;
        }
        return sectionWithUpStation;
    }

    private void checkSectionDistanceValidity(int oldDistance, int distance) {
        if (oldDistance <= distance) {
            throw new RuntimeException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }

    public void remove(Line line, Station station) {
        checkSectionRemoveValidity(station);
        List<Section> sectionsToRemove = getSectionToRemove(station);
        if (sectionsToRemove.size() > 1) {
            sections.removeAll(sectionsToRemove);
            int newDistance = sectionsToRemove.stream().mapToInt(it -> it.getDistance()).sum();
            sections.add(new Section(line, sectionsToRemove.get(0).getUpStation(), sectionsToRemove.get(1).getDownStation(), newDistance));
            return;
        }
        getSections().stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst()
                .ifPresent(it -> getSections().remove(it));
    }

    private void checkSectionRemoveValidity(Station station) {
        if (sections.size() <= 1) {
            throw new RuntimeException("마지막 남은 구간은 삭제할 수 없습니다.");
        }
        if (sections.stream().noneMatch(it -> it.getDownStation().equals(station) || it.getUpStation().equals(station))) {
            throw new RuntimeException("등록되지 않은 역은 삭제할 수 없습니다.");
        }
    }

    private List<Section> getSectionToRemove(Station station) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(station) || it.getDownStation().equals(station))
                .collect(Collectors.toList());
    }

}
