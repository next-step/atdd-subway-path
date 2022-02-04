package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("id")
    private List<Section> sections = new ArrayList<>();

    public boolean isEmpty() {
        return sections == null || sections.isEmpty();
    }

    public int count() {
        return sections.size();
    }

    public List<Section> getSectionList() {
        return sections;
    }

    public void addSection(Section section) {
        if (!isEmpty()) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();

            List<Long> stationIds = getStationIds();

            boolean isUpStationExisted = stationIds.stream().anyMatch(it -> it == upStationId);
            boolean isDownStationExisted = stationIds.stream().anyMatch(it -> it == downStationId);

            checkAddValidate(upStationId, downStationId, stationIds, isUpStationExisted, isDownStationExisted);

            if (isUpStationExisted) {
                updateUpSection(section);
            }

            if (isDownStationExisted) {
                updateDownSection(section);
            }
        }
        sections.add(section);
    }

    public void removeSection(Station station) {
        if (isEmpty() || count() == 1) {
            throw new IllegalArgumentException();
        }

        Optional<Section> downStation = sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst();

        Optional<Section> upStation = sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst();

        if (downStation.isPresent() && upStation.isPresent()) {
            Station addUpStation = downStation.get().getUpStation();
            Station addDownStation = upStation.get().getDownStation();

            int addDistance = upStation.get().getDistance() + downStation.get().getDistance();
            sections.add(new Section(upStation.get().getLine(), addUpStation, addDownStation, addDistance));
        }

        downStation.ifPresent(it -> sections.remove(it));
        upStation.ifPresent(it -> sections.remove(it));
    }

    public List<Station> getStationList() {
        if (isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.isSameUpStation(finalDownStation))
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private List<Long> getStationIds() {
        if (isEmpty()) {
            return Arrays.asList();
        }

        return sections.stream().map(it -> Arrays.asList(it.getUpStation().getId(), it.getDownStation().getId()))
                .flatMap(it -> it.stream()).distinct()
                .collect(Collectors.toList());
    }

    private void checkAddValidate(Long upStationId, Long downStationId, List<Long> stationIds, boolean isUpStationExisted, boolean isDownStationExisted) {
        if (isUpStationExisted && isDownStationExisted) {
            throw new RuntimeException("이미 등록된 구간 입니다.");
        }

        if (stationIds.stream().noneMatch(it -> it == upStationId) && stationIds.stream().noneMatch(it -> it == downStationId)) {
            throw new RuntimeException("등록할 수 없는 구간 입니다.");
        }
    }

    private void updateDownSection(Section section) {
        sections.stream()
                .filter(it -> it.isSameDownStation(section.getDownStation()))
                .findFirst()
                .ifPresent(it -> it.updateDownStation(section.getUpStation(), section.getDistance()));
    }

    private void updateUpSection(Section section) {
        sections.stream()
                .filter(it -> it.isSameUpStation(section.getUpStation()))
                .findFirst()
                .ifPresent(it -> it.updateUpStation(section.getDownStation(), section.getDistance()));
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.isSameDownStation(finalDownStation))
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }
        return downStation;
    }
}
