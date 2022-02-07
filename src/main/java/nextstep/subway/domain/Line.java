package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.NotRegisterStation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
        return addSection(new Section(
                this,
                upStation,
                downStation,
                distance
        ));
    }

    private List<Section> addSection(Section newSection) {

        AtomicBoolean normalCondition = new AtomicBoolean(true);

        isIncludingOne(newSection);

        checkDuplicateSections(newSection);

        insertBetweenSameUpStations(
                newSection,
                normalCondition
        );

        insertBetweenSameDownStations(
                newSection,
                normalCondition
        );

        insertLastUpStation(
                newSection,
                normalCondition
        );

        insertLastDownStation(
                newSection,
                normalCondition
        );

        return sections;
    }

    private void isIncludingOne(Section newSection) {
        getStations().stream()
                .filter(station -> !getStations().contains(newSection.getUpStation()))
                .filter(section -> !getStations().contains(newSection.getDownStation()))
                .findAny()
                .ifPresent(unused -> {
                    throw new NotRegisterStation("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
                });
    }

    private void checkDuplicateSections(Section newSection) {

        Station prevDownStation = null;


        for (Section section : sections) {
            Station upStation = section
                    .getUpStation();
            Station downStation = section
                    .getDownStation();

            if (upStation.equals(newSection.getUpStation()) && downStation.equals(newSection.getDownStation())) {
                throw new NotRegisterStation("이미 동일한 구간정보가 등록돼있어서 취소됐습니다.");
            }

            if (upStation.equals(prevDownStation)) {
                if (downStation.equals(newSection.getDownStation())) {
                    throw new NotRegisterStation("요청한 구간정보가 이미 간접적으로 연결돼있어서 취소됐습니다.");
                }
            }
            prevDownStation = downStation;
        }

    }

    private void insertLastDownStation(Section newSection, AtomicBoolean normalCondition) {
        if (normalCondition.get()) {
            sections.add(newSection);
        }
    }

    private void insertLastUpStation(Section newSection, AtomicBoolean normalCondition) {
        sections.stream()
                .filter(unused -> normalCondition.get())
                .filter(oldSection -> oldSection.getUpStation()
                        .equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(
                            sections.indexOf(oldSection),
                            newSection
                    );
                });
    }

    private void insertBetweenSameDownStations(Section newSection, AtomicBoolean normalCondition) {
        sections.stream()
                .filter(unused -> normalCondition.get())
                .filter(oldSection -> oldSection.getDownStation()
                        .equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {

                    normalCondition.set(false);

                    if (newSection.getDistance() < oldSection.getDistance()) {
                        sections.add(new Section(
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
                    } else if (newSection.getDistance() > oldSection.getDistance()) {
                        throw new NotRegisterStation("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크면 등록을 할 수 없음");
                    } else if (newSection.getDistance() == oldSection.getDistance()) {
                        throw new NotRegisterStation("역 사이에 새로운 역을 등록할 경우 기존 역 사이와 길으면 등록을 할 수 없음");
                    }
                });
    }

    private void insertBetweenSameUpStations(Section newSection, AtomicBoolean normalCondition) {
        sections.stream()
                .filter(oldSection -> oldSection.getUpStation()
                        .equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {

                    normalCondition.set(false);

                    //새로운 구간의 거리가 작은 경우
                    if (newSection.getDistance() < oldSection.getDistance()) {
                        //[기존구간-새로운 구간]으로 구간을 생성한다.
                        sections.add(new Section(
                                this,
                                newSection.getDownStation(),
                                oldSection.getDownStation(),
                                (oldSection.getDistance() - newSection.getDistance())
                        ));
                        //기존 구간을 새로운 구간으로 교체한다.
                        oldSection.update(newSection);
                    } else if (newSection.getDistance() > oldSection.getDistance()) {
                        sections.add(new Section(
                                this,
                                oldSection.getDownStation(),
                                newSection.getDownStation(),
                                (newSection.getDistance() - oldSection.getDistance())
                        ));
                    } else {
                        throw new RuntimeException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                    }
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

    public int sizeOfSection(){
        return getSections().size();
    }

    public void removeSection(int index){
        getSections().remove(index);
    }

}
