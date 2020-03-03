package atdd.station.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.StringJoiner;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public Station(String name) {
        this.name = name;
    }

    @Builder(builderMethodName = "testBuilder")
    protected Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    public static Station emptyStation() {
        return new Station();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Station.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }

}
