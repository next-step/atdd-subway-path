package atdd.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder @Getter @AllArgsConstructor
@NoArgsConstructor
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String name;
}
