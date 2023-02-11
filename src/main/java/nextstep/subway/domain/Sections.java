package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotFoundException;
import nextstep.subway.domain.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        findUpEndSection().ifPresent(upEndSection -> {
            stations.add(upEndSection.getUpStation());

            Section nextSection = upEndSection;
            while (nextSection != null) {
                stations.add(nextSection.getDownStation());
                nextSection = findNextOf(nextSection);
            }
        });

        return stations;
    }

    private Section findNextOf(Section section) {
        return sections.stream()
                .filter(it -> it.getUpStation() == section.getDownStation())
                .findFirst()
                .orElse(null);
    }

    public void add(Section section) {
        if (sections.isEmpty() || isEndSection(section)) {
            sections.add(section);
            return;
        }

        validate(section);

        addBetweenSection(section);
        sections.add(section);
    }

    private boolean isEndSection(Section section) {
        Section upEndSection = findUpEndSection().orElseThrow(() -> new NotFoundException("상행 종점 구간을 찾을 수 없습니다."));
        Section downEndSection = findDownEndSection().orElseThrow(() -> new NotFoundException("하행 종점 구간을 찾을 수 없습니다."));

        return upEndSection.getUpStation() == section.getDownStation() ||
                downEndSection.getDownStation() == section.getUpStation();
    }

    private void validate(Section section) {
        if (containAllStations(section)) {
            throw new SubwayException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }


        if (isNotContainedAnyStations(section)) {
            throw new SubwayException("상행역과 하행역 둘 중 하나라도 포함되어야 합니다.");
        }
    }

    private boolean containAllStations(Section section) {
        return sections.stream()
                .anyMatch(it -> it.isSameStations(section));
    }

    private boolean isNotContainedAnyStations(Section section) {
        List<Station> stations = stations();
        return !stations.contains(section.getUpStation()) &&
                !stations.contains(section.getDownStation());
    }

    private Optional<Section> findUpEndSection() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findDownEndSection() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findFirst();
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

    public void remove(long stationId) {
        if (sections.size() <= 1) {
            throw new SubwayException("구간이 1개 이하인 경우 삭제할 수 없습니다.");
        }

        List<Section> deleteSections = sections.stream()
                .filter(it -> it.hasStationId(stationId))
                .collect(Collectors.toList());

        if (deleteSections.isEmpty()) {
            throw new NotFoundException(stationId + "번 역을 찾을 수 없습니다.");
        }

        if (deleteSections.size() == 2) {
            Section mergedSection = Section.merge(deleteSections.get(0), deleteSections.get(1));
            sections.add(mergedSection);
        }

        sections.removeAll(deleteSections);
    }
}
