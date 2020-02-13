package atdd.station.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Station {

	@Id
	@GeneratedValue
	@Column(name = "STATION_ID")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Builder
	public Station(final String name) {
		this.name = name;
	}
}
