package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.BadRequestSectionsException;
import nextstep.subway.exception.NullPointerSectionsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class LineSections {

    private static final int MIN_LIMIT = 1;


    private LineSections(Section section) {
        sections.add(section);
    }

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderColumn(name = "POSITION")
    private List<Section> sections = new ArrayList<>();

    public static LineSections init(Section section) {
        return new LineSections(section);
    }

    public List<Section> add(Section newSection) {
        checkRequestValidation(newSection);

        if (isAddableFirstSections(newSection)) {
            sections.add(0, newSection);
            return sections;
        }
        if (isAddableLastSections(newSection)) {
            sections.add(newSection);
            return sections;
        }
        addMiddleSections(newSection);

        return sections;
    }

    public void remove(Station station) {
        checkRemovableSections(station);

        Optional<Section> frontSection = findSectionByDownStation(station);
        Optional<Section> backSection = findSectionByUpStation(station);

        if (!frontSection.isPresent() && backSection.isPresent()) {
            sections.remove(backSection.get());
        }
        if (frontSection.isPresent() && backSection.isPresent()) {
            Section front = frontSection.get();
            Section back = backSection.get();
            int distance = front.getDistance() + back.getDistance();
            Section newSection = Section.builder()
                    .line(front.getLine())
                    .upStation(front.getUpStation())
                    .downStation(back.getDownStation())
                    .distance(distance)
                    .build();
            sections.remove(back);
            sections.add(newSection);
            sections.remove(front);
        }
        if (frontSection.isPresent() && !backSection.isPresent()) {
            sections.remove(frontSection.get());
        }
    }

    private void checkRemovableSections(Station station) {
        if (sections.size() <= MIN_LIMIT) {
            throw new BadRequestSectionsException("노선에는 하나 이상의 구간이 존재해야합니다.");
        }
        if (!getStations().contains(station)) {
            throw new BadRequestSectionsException("노선에 포함되지 않은 구간은 삭제가 불가능합니다.");
        }
    }


    public List<Station> getStations() {
        Section section = getFirstSections();
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        for (int i = 0; i <= sections.size() - 2; i++) {
            Station curDownStation = section.getDownStation();
            section = findSectionByUpStation(curDownStation)
                    .orElseThrow(() -> new IllegalArgumentException("노선의 구간 정보가 비정상적인 상태입니다."));
            stations.add(section.getUpStation());
        }
        stations.add(section.getDownStation());

        return stations;
    }

    public int getTotalDistancs() {
        return sections.stream().mapToInt(section -> section.getDistance()).sum();
    }

    private void addMiddleSections(Section newSection) {
        Section orgSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new BadRequestSectionsException("상행선 정보가 잘못되었습니다."));

        if (orgSection.getDistance() <= newSection.getDistance()) {
            throw new BadRequestSectionsException("구간 사이의 새 구간의 길이는 기존 구간의 길이와 같거나 초과할 수 없습니다.");
        }

        int distance = orgSection.getDistance() - newSection.getDistance();
        Section changeSections = Section.builder()
                .line(orgSection.getLine())
                .upStation(newSection.getDownStation())
                .downStation(orgSection.getDownStation())
                .distance(distance)
                .build();

        sections.remove(orgSection);
        sections.add(newSection);
        sections.add(changeSections);
    }

    private boolean isAddableLastSections(Section newSection) {
        Station lastStation = sections.get(sections.size() - 1).getDownStation();
        return newSection.getUpStation().equals(lastStation);
    }

    private boolean isAddableFirstSections(Section newSection) {
        Station firstStation = sections.get(0).getUpStation();
        return newSection.getDownStation().equals(firstStation);
    }

    private Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream().filter(section -> station.equals(section.getUpStation())).findAny();
    }

    private Optional<Section> findSectionByDownStation(Station station) {
        return sections.stream().filter(section -> station.equals(section.getDownStation())).findAny();
    }

    private Section getFirstSections() {
        return sections.stream()
                .filter(it -> !sections
                        .stream()
                        .anyMatch(temp -> it.getUpStation().equals(temp.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new NullPointerSectionsException("해당 노선의 상행 종점역을 찾을 수 없습니다."));
    }

    private Section getLastSections() {
        return sections.stream()
                .filter(it -> !sections
                        .stream()
                        .anyMatch(temp -> it.getDownStation().equals(temp.getUpStation())))
                .findFirst()
                .orElseThrow(() -> new NullPointerSectionsException("해당 노선의 하행 종점역을 찾을 수 없습니다."));
    }


    private void checkRequestValidation(Section newSection) {
        if (sections.isEmpty()) {
            return;
        }
        boolean isExistUpStation = getStations().stream().anyMatch(it -> it.equals(newSection.getUpStation()));
        boolean isExistDownStation = getStations().stream().anyMatch(it -> it.equals(newSection.getDownStation()));
        if (isExistUpStation && isExistDownStation) {
            throw new BadRequestSectionsException("상행선과 하행선이 이미 해당 노선에 등록되어있는 역입니다.");
        }
        if (!isExistUpStation && !isExistDownStation) {
            throw new BadRequestSectionsException("상행선과 하행선이 모두 해당 노선에 등록되어있지 않은 역입니다.");
        }
    }

}