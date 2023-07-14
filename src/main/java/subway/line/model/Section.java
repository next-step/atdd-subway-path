package subway.line.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import subway.exception.SubwayBadRequestException;
import subway.station.model.Station;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void changeDownStation(Section newSection) {
        validDistance(newSection.getDistance());
        this.distance = this.getDistance() - newSection.getDistance();
        this.downStation = newSection.getUpStation();
    }

    public void changeUpStation(Section newSection) {
        validDistance(newSection.getDistance());
        this.distance = this.getDistance() - newSection.getDistance();
        this.upStation = newSection.getDownStation();
        // question comment : 혹시 영속성을 추적할 수 있는 좋은 방법이 있을까요? 이게 일단 되는데 왜 되는지 모르겠습니다..
        // 과정을 짚어보면 newSection은 아직 persist되기 전인데 어째서 이렇게 해서 지정이 되는지 이해가 안됩니다.
        // 영속성 전이 나중에 해결하자고 일단 내버려뒀는데 뒤로 걷다 얻어걸렸습니다.
    }

    private void validDistance(long newDistance) {
        if (this.getDistance() <= newDistance){
            throw new SubwayBadRequestException(9999L, "새로 추가되는 구간의 길이는 기존 구간보다 같거나 더 길 수 없습니다.");// TODO : constant
        }

    }
}
