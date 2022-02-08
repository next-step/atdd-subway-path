package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.NotRegisterSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = addSection(
                upStation,
                downStation,
                distance
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> addSection(Station upStation, Station downStation, int distance) {
        return addSection(new Section(this, upStation, downStation, distance));
    }

    private List<Section> addSection(Section newSection) {
        isIncludingOne(newSection);
        isDuplicate(newSection);
        isBigOrEqualTo(newSection);

        emptySection(newSection);

        List<Section> copySections = new ArrayList<>(sections);
        Collections.copy(copySections, sections);

        comparing(upStationIsTheEqual(copySections, newSection));
        comparing(downStationIsTheEqual(copySections, newSection));
        comparing(insertLastUpStation(copySections, newSection));
        comparing(insertLastDownStation(copySections, newSection));

        return sections;
    }

    private void emptySection(Section newSection) {
        sections.add(newSection);
    }

    private void comparing(List<Section> copySections) {
        if (!sections.equals(copySections)) {
            Collections.copy(sections, copySections);
            copySections = new ArrayList<>(sections);
            Collections.copy(copySections, sections);
        }
    }

    // TODO. [피드백]: ifPresent는 예외 처리할 때 사용하지 않습니다. orElseThrow로 적용해보시기 바랍니다.
    // Stream이 익숙하지 않아서, 멀티 filter를 제어해서 orElseThrow 사용 하는법을 모르겠음..
    private void isIncludingOne(Section newSection) {
        getStations().stream()
                .filter(station -> !getStations().contains(newSection.getUpStation()))
                .filter(station -> !getStations().contains(newSection.getDownStation()))
                .findFirst()
                .ifPresent(unused -> {
                    throw new NotRegisterSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
                });
    }

    private void isDuplicate(Section newSection) {

        Station prevDownStation = null;


        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            if (upStation.equals(newSection.getUpStation()) && downStation.equals(newSection.getDownStation())) {
                throw new NotRegisterSectionException("이미 동일한 구간정보가 등록돼있어서 취소됐습니다.");
            }

            if (upStation.equals(prevDownStation)) {
                if (downStation.equals(newSection.getDownStation())) {
                    throw new NotRegisterSectionException("요청한 구간정보가 이미 간접적으로 연결돼있어서 취소됐습니다.");
                }
            }
            prevDownStation = downStation;
        }

    }

    private List<Section> insertLastDownStation(List<Section> copySections, Section newSection) {
        copySections.stream()
                .filter(oldSection -> oldSection.getDownStation()
                        .equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    copySections.add(newSection);
                });
        return copySections;
    }

    private List<Section> insertLastUpStation(List<Section> copySections, Section newSection) {
        copySections.stream()
                .filter(oldSection -> oldSection.getUpStation()
                        .equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    copySections.add(sections.indexOf(oldSection), newSection);
                });
        return copySections;
    }

    private List<Section> downStationIsTheEqual(List<Section> copySections, Section newSection) {
        copySections.stream()
                .filter(oldSection -> oldSection.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    copySections.add(new Section(
                            this,
                            newSection.getUpStation(),
                            oldSection.getDownStation(),
                            newSection.getDistance()
                    ));
                    oldSection.update(new Section(
                            this,
                            oldSection.getUpStation(),
                            newSection.getUpStation(),
                            (oldSection.getDistance() - newSection.getDistance())
                    ));
                });

        return copySections;
    }

    private List<Section> upStationIsTheEqual(List<Section> copySections, Section newSection) {
        copySections.stream()
                .filter(oldSection -> oldSection.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .filter(oldSection -> newSection.getDistance() < oldSection.getDistance())
                .ifPresent(oldSection -> {
                    copySections.add(new Section(
                            this,
                            newSection.getDownStation(),
                            oldSection.getDownStation(),
                            (oldSection.getDistance() - newSection.getDistance())
                    ));
                    oldSection.update(newSection);
                });

        return copySections;
    }

    private void isBigOrEqualTo(Section newSection) {
        Predicate<Section> greater = oldSection -> newSection.getDistance() > oldSection.getDistance();
        Predicate<Section> equal = oldSection -> newSection.getDistance() == oldSection.getDistance();

        sections.stream()
                .filter(oldSection -> oldSection.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .filter(greater.or(equal))
                .ifPresent(unused -> {
                    throw new NotRegisterSectionException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없음");
                });
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station prevDownStation = null;

        for (int idx = 0; idx < sections.size(); idx++) {
            Station upStation = sections.get(idx)
                    .getUpStation();
            Station downStation = sections.get(idx)
                    .getDownStation();
            if (idx == 0) {
                stations.add(upStation);
                stations.add(downStation);
            }
            if (upStation.equals(prevDownStation)) {
                stations.add(downStation);
            }
            prevDownStation = downStation;
        }

        return stations;
    }

    public void removeSection() {
        Section lastSection = sections.get(sections.size() - 1);
        sections.remove(lastSection);
    }

    public Station getLastDownStation() {
        return getLastSection().getDownStation();
    }

    public Section getLastSection() {
        int lastIndex = sizeOfSection() - 1;
        return getSections().get(lastIndex);
    }

    public int sizeOfSection() {
        return getSections().size();
    }

    public void removeSection(int index) {
        getSections().remove(index);
    }

}
