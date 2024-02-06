package nextstep.subway.api.domain.model.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.common.exception.SectionInsertionNotValidException;

/**
 * @author : Rene Choi
 * @since : 2024/01/27
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Section implements Comparable<Section> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "up_station_id")
	private Station upStation;

	@ManyToOne
	@JoinColumn(name = "down_station_id")
	private Station downStation;

	@Column(nullable = false)
	private Long distance;

	/**
	 * Section의 동등성은 상행, 하행, 거리로 판단합니다.
	 * 상행역, 하행역, 거리가 모두 같을 경우 해당 Section은 같은 객체로 평가합니다.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(upStation, section.upStation) &&
			Objects.equals(downStation, section.downStation) &&
			Objects.equals(distance, section.distance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(upStation, downStation, distance);
	}

	/**
	 * 섹션에 대한 정렬은 UpStation id를 기준으로
	 */
	@Override
	public int compareTo(Section other) {
		return this.fetchUpStationId().compareTo(other.fetchUpStationId());
		// return this.id.compareTo(other.id);
	}

	public static Section of(Station upStation, Station downStation, Long distance) {
		return Section.builder()
			.upStation(upStation)
			.downStation(downStation)
			.distance(distance)
			.build();
	}

	public Long fetchUpStationId() {
		return this.upStation.getId();
	}

	public Long fetchDownStationId() {
		return this.downStation.getId();
	}

	public String fetchUpStationName() {
		return this.upStation.getName();
	}

	public String fetchDownStationName() {
		return this.downStation.getName();
	}

	public boolean isDownEndStation(String stationName) {
		return this.fetchDownStationName().equals(stationName);
	}

	public boolean isUpEndStation(String stationName) {
		return this.fetchUpStationName().equals(stationName);
	}

	public boolean isDownEndStation(Long stationId) {
		return this.fetchDownStationId().equals(stationId);
	}

	public boolean isUpEndStation(Long stationId) {
		return this.fetchUpStationId().equals(stationId);
	}

	public boolean isAnyStation(Long stationId) {
		return isDownEndStation(stationId) || isUpEndStation(stationId);
	}

	public boolean isBothStationSame(Station upStation, Station downStation) {
		return isSameUpStation(upStation) && isSameDownStation(downStation);
	}

	public boolean isSameUpStation(Station upStation) {
		return this.upStation.equals(upStation);
	}

	public boolean isSameDownStation(Station downStation) {
		return this.downStation.equals(downStation);
	}

	public void validateInsertion(Section other) {
		if (this.isBothStationSame(other.getUpStation(), other.getDownStation())) {
			throw new SectionInsertionNotValidException("이미 등록된 구간입니다.");
		}
	}

	public boolean canInsertBetween(Section newSection) {
		return this.isSameUpStation(newSection.getUpStation()) || this.isSameDownStation(newSection.getDownStation());
	}

	public Section adjustForUpMatch(Section newSection) {
		return Section.builder()
			.upStation(newSection.getDownStation())
			.downStation(this.getDownStation())
			.distance(calculateInsertionDistance(newSection))
			.build();
	}

	public Section adjustForDownMatch(Section newSection) {
		return Section.builder()
			.upStation(this.getUpStation())
			.downStation(newSection.getUpStation())
			.distance(calculateInsertionDistance(newSection))
			.build();
	}

	private long calculateInsertionDistance(Section newSection) {
		return this.getDistance() - newSection.getDistance();
	}

}
