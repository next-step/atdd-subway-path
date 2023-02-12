package nextstep.subway.common;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Section;

public class SectionFixtures {

	public static SectionRequest 구간_추가_요청(Long upStationId, Long downStationId, int distance) {
		return new SectionRequest(upStationId, downStationId, distance);
	}

	public static void insertSectionIds(List<Section> sections) throws Exception {
		for (int i = 1; i <= sections.size(); i++) {
			Section target = sections.get(i - 1);
			Field idField = ReflectionUtils.findField(target.getClass(), "id");
			idField.setAccessible(true);
			idField.set(target, (long)i);
		}
	}
}
