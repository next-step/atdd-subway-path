package subway.line.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.station.model.Station;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Line {

    private static final long MINIMAL_SECTION_SIZE = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn
    private Station upStation;

    @ManyToOne
    @JoinColumn
    private Station downStation;

    @Builder.Default
    @Embedded
    private LineSections lineSections = new LineSections();

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) { // TODO : 바뀐 스팩의 상행역/하행역 지정 반영하기
        this.lineSections.add(section, this);
        this.downStation = section.getDownStation();
    }

    public List<Station> getStations() {
        return lineSections.getStations(this.upStation, this.downStation);
    }

    public long getSectionsCount() { // TODO : 다시 안으로 넣기 ->  테스트 말고 쓰는데 없음
        return lineSections.getSectionsCount();
    }

    public void deleteSectionByStation(Station station) {
        Section lastSection = lineSections.removeSectionByStation(station);
        this.downStation = lastSection.getUpStation();
    }
}
