package nextstep.subway.api.domain.dto.outport;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Rene Choi
 * @since : 2024/02/02
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionInfos implements Iterable<SectionInfo> {

	private SortedSet<SectionInfo> sections = new TreeSet<>();

	@Override
	public Iterator<SectionInfo> iterator() {
		return sections.iterator();
	}
}
