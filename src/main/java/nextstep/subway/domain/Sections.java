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

    public List<Section> getSections() {
        return sections;
    }

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

        addUpSection(section);
        addDownSection(section);
        sections.add(section);
    }

    private void validate(Section section) {
        List<Station> stations = stations();

        if (sections.stream().anyMatch(it -> it.isSameStations(section))) {
            throw new SubwayException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (!stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation())) {
            throw new SubwayException("상행역과 하행역 둘 중 하나라도 포함되어야 합니다.");
        }
    }

    private boolean isEndSection(Section section) {
        Section upEndSection = findUpEndSection().orElseThrow(() -> new NotFoundException("상행 종점 구간을 찾을 수 없습니다."));
        Section downEndSection = findDownEndSection().orElseThrow(() -> new NotFoundException("하행 종점 구간을 찾을 수 없습니다."));

        return upEndSection.getUpStation() == section.getDownStation() ||
                downEndSection.getDownStation() == section.getUpStation();
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

    private void addUpSection(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.isSameDownStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(oldSection.up(section));
                    sections.remove(oldSection);
                });
    }

    private void addDownSection(Section section) {
        sections.stream()
                .filter(oldSection -> oldSection.isSameUpStation(section))
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(oldSection.down(section));
                    sections.remove(oldSection);
                });
    }

    public void remove(long stationId) {
        if (sections.size() == 1) {
            throw new SubwayException("구간이 1개인 경우 삭제할 수 없습니다.");
        }

        Section lastSection = findDownEndSection().orElseThrow(() -> new NotFoundException("마지막 구간을 찾을 수 없습니다."));
        if (!lastSection.isDownStationId(stationId)) {
            throw new SubwayException("마지막 구간만 제거할 수 있습니다.");
        }

        sections.remove(lastSection);
    }
}
