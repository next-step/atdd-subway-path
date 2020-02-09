package atdd.station;

import lombok.Builder;
import sun.jvm.hotspot.gc.shared.Generation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String name;
}
