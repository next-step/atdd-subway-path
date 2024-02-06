package nextstep.subway.api.domain.dto.outport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.model.entity.Section;
import nextstep.subway.common.mapper.ObjectMapperBasedObjectMapper;

/**
 * @author : Rene Choi
 * @since : 2024/01/31
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionInfo implements Comparable<SectionInfo> {

	private Long id;

	private StationInfo upStation;

	private StationInfo downStation;

	private Long distance;


	@Override
	public int compareTo(SectionInfo other) {
		return this.id.compareTo(other.id);
	}

	public static SectionInfo from(Section section) {
		return ObjectMapperBasedObjectMapper.convert(section, SectionInfo.class);
	}
}
