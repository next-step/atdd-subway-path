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

    public void add(Section section) {
        if (!isEmpty()) {
            Long upStationId = section.getUpStation().getId();
            Long downStationId = section.getDownStation().getId();

            List<Long> stationIds = getStationIds();

            boolean isUpStationExisted = stationIds.stream().anyMatch(it -> it == upStationId);
            boolean isDownStationExisted = stationIds.stream().anyMatch(it -> it == downStationId);

            if (isUpStationExisted && isDownStationExisted) {
                throw new RuntimeException("이미 등록된 구간 입니다.");
            }

            if (stationIds.stream().noneMatch(it -> it == upStationId) && stationIds.stream().noneMatch(it -> it == downStationId)) {
                throw new RuntimeException("등록할 수 없는 구간 입니다.");
            }

            if (isUpStationExisted) {
                updateUpSection(section);
            }

            if (isDownStationExisted) {
                updateDownSection(section);
            }
        }

        sections.add(section);
    }

    public int count() {
        return sections.size();
    }

    public void removeLastStation(Station station) {
        if (isEmpty())
            throw new IllegalArgumentException();

        if (!sections.get(sections.size() - 1).isSameDownStation(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
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
