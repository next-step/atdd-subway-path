package subway.line.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import subway.exception.SubwayBadRequestException;
import subway.line.constant.SubwayMessage;
import subway.station.model.Station;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Line line;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Station downStation;

    @Column(nullable = false)
    private Long distance;


    public void addDownStation(Section newSection) {
        validDistance(newSection.getDistance());
        this.distance = this.getDistance() - newSection.getDistance();
        this.downStation = newSection.getUpStation();
    }


    public void addUpStation(Section newSection) {
        validDistance(newSection.getDistance());
        this.distance = this.getDistance() - newSection.getDistance();
        this.upStation = newSection.getDownStation();
    }


    // TODO : 구간 단위 테스트 작성하기
    public void moveDownStationFromTargetSection(Section targetSection) {
        this.downStation = targetSection.getDownStation();
        this.distance = this.distance + targetSection.getDistance();
    }

    private void validDistance(long newDistance) {
        if (this.getDistance() <= newDistance) {
            throw new SubwayBadRequestException(SubwayMessage.SECTION_OVER_DISTANCE);
        }

    }
}
