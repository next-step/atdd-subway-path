package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public void add(Section section) {
        // 1. 새로운 구간 상하행역이 노선에 전혀 포함되어 있지 않거나 둘 다 포함되어 있는 경우 IllegalArgumentException
        // 2-1. 구간 내 상행역과 새로운 구간의 상행역이 같은경우
        // 2-2. 새로운 구간의 Distance >= 현재 같은 상행역을 가진 구간의 Distance IllegalArgumentException
        // 2-3. 2-2를 통과했다면, 새로운 구간 두개를 생성하고 원래 구간을 삭제한다. 이 때, 새로운 구간의 길이는 원래 구간의 길이를 나눠가진다.
        // 3-1. 상행 종점의 상행역과 새로운 구간의 하행역이 같은 경우
        // 3-2. 새로운 상행 종점 생성
        // 4-1. 하행 종점의 하행역과 새로운 구간의 상행역이 같은 경우
        // 4-2. 새로운 하행 종점 생성



    }

    public List<Station> getStation() {
        // 1. 상행 종점을 찾는다.
        // 1-2. 상행 종점의 상행역을 저장한다. List<Station>을 생성하여 추가한다.
        // 2. 현 구간의 하행역과 구간의 상행역이 같은 구간을 찾는다.
        // 2-2. 현 구간의 하행역을 List<Station>에 추가한다.
        // 4. 2 번 과정을 반복한다. 구간의 마지막까지 반복한다.


        return null;
    }
}
