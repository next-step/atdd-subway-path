package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Station(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
// Entity의 특성을 그대로 따라 id만을 가지고 식별자 동등성으로 판별하게 되면.. 다른 로직(sections)들이 너무 먼 길을 돌아갈 것 같습니다..ㅠ
// 이럴땐 어떻게 해야될까요? VO로 변환하여 구조적 동등성 비교를 해야할까요? 아니면 그냥 아래 방법대로 equals()에 name까지 열어줄까요..
        return Objects.equals(getId(), station.getId()) && Objects.equals(getName(), station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
