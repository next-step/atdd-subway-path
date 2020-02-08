package atdd.station;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Station {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@Builder
	public Station(final String name) {
		this.name = name;
	}
}
