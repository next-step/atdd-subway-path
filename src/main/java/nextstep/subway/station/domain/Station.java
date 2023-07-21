package nextstep.subway.station.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    public Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Hibernate Proxy equals 참고 https://tecoble.techcourse.co.kr/post/2022-10-17-jpa-hibernate-proxy/
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.getId());  // 프록시의 id값은 null이다. 프록시 내부의 인터셉터가 id값을 가지고 있기 때문. 그러므로 getId()로 초기화하자.
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
