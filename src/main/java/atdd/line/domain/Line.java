package atdd.line.domain;

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
public class Line {

	@Id
	@GeneratedValue
	@Column(name = "LINE_ID")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "START_TIME")
	private String startTime;

	@Column(name = "END_TIME")
	private String endTime;

	@Column(name = "INTERVAL_TIME")
	private String intervalTime;

	@Builder
	public Line(final String name, final String startTime, final String endTime, final String intervalTime) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
	}
}
