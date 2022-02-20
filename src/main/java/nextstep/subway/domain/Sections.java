package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.exception.SubwayException;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
	private List<Section> sections = new ArrayList<>();

	public Sections() {

	}

	public void add(Section section) {
		if (this.sections.isEmpty()) {
			this.sections.add(section);
			return;
		}

		// 중복확인
		isDuplicatedSection(section);
		// 존재하지 않는 기준역 확인
		isNotExistUpOrDownStation(section);
		updateUpSection(section);
		updateDownSection(section);

		this.sections.add(section);
	}

	public void delete(Station station) {
		if (this.sections.size() <= 1) {
			throw new SubwayException.CanNotDeleteException("구간이 하나일 때는 지울 수 없습니다");
		}
		checkNotExistStation(station);

		Optional<Section> upSection = findUpSection(station);
		Optional<Section> downSection = findDownSection(station);

		connectStations(upSection, downSection);

		deleteUpSection(upSection);
		deleteDownSection(downSection);
	}

	public List<Station> findAllStations() {
		//  첫번째 상행역을 찾고, 그 뒤로 구간에 연결되는 하행역을 찾는다
		List<Station> stations = new ArrayList<>();
		Station upStation = findFirstUpStation();
		stations.add(upStation);

		while (true) {
			Station station = upStation;
			Optional<Section> section = findUpSection(station);
			if (!section.isPresent()) {
				break;
			}
			// 다음 상행역은 구간에서 찾은 하행역
			upStation = section.get().getDownStation();
			stations.add(upStation);
		}

		return stations;
	}

	public int size() {
		return this.sections.size();
	}

	public List<Section> findAll() {
		return this.sections;
	}

	public Section findByIndex(int index) {
		return this.sections.get(index);
	}

	public void removeByIndex(int index) {
		this.sections.remove(index);
	}

	private void updateDownSection(Section section) {
		findUpSection(section.getUpStation())
			.ifPresent(it -> {
				sections.add(new Section(section.getLine(), section.getDownStation(), it.getDownStation(),
					it.getDistance() - section.getDistance()));
				sections.remove(it);
			});
	}

	private void updateUpSection(Section section) {
		findDownSection(section.getDownStation())
			.ifPresent(it -> {
				sections.add(new Section(section.getLine(), it.getUpStation(), section.getUpStation(),
					it.getDistance() - section.getDistance()));
				sections.remove(it);
			});
	}

	private void isDuplicatedSection(Section section) {
		this.sections.stream()
			.filter(it -> it.isDuplicateSection(section.getUpStation(), section.getDownStation()))
			.findFirst()
			.ifPresent(it -> {
				throw new SubwayException.DuplicatedException();
			});
	}

	private void isNotExistUpOrDownStation(Section section) {
		this.sections.stream()
			.filter(it -> !containsStation(section, it))
			.findFirst()
			.ifPresent(it -> {
				throw new SubwayException.WrongParameterException();
			});
	}

	private boolean containsStation(Section section, Section it) {
		return it.isEqualUpStation(section.getUpStation())
			|| it.isEqualUpStation(section.getDownStation())
			|| it.isEqualDownStation(section.getUpStation())
			|| it.isEqualDownStation(section.getDownStation());
	}

	private Station findFirstUpStation() {
		List<Station> upStations = this.sections.stream()
			.map(Section::getUpStation)
			.collect(Collectors.toList());

		List<Station> downStations = this.sections.stream()
			.map(Section::getDownStation)
			.collect(Collectors.toList());

		return upStations.stream()
			.filter(station -> !downStations.contains(station))
			.findFirst()
			.orElseThrow(SubwayException.NotFoundException::new);
	}

	private Optional<Section> findDownSection(Station station) {
		return this.sections.stream()
			.filter(it -> it.isEqualDownStation(station))
			.findFirst();
	}

	private Optional<Section> findUpSection(Station station) {
		return this.sections.stream()
			.filter(it -> it.isEqualUpStation(station))
			.findFirst();
	}

	private void checkNotExistStation(Station station) {
		Optional<Station> stationOpt = findAllStations().stream()
			.filter(it -> it.equals(station))
			.findFirst();

		if (!stationOpt.isPresent()) {
			throw new SubwayException.CanNotDeleteException("존재하지 않는 역은 지울 수 없습니다.");
		}
	}

	private void connectStations(Optional<Section> upSection, Optional<Section> downSection) {
		if (upSection.isPresent() && downSection.isPresent()) {
			Section section = new Section(
				upSection.get().getLine(),
				downSection.get().getUpStation(),
				upSection.get().getDownStation(),
				upSection.get().getDistance() + downSection.get().getDistance()
			);
			this.sections.add(section);
		}
	}

	private void deleteDownSection(Optional<Section> downSection) {
		downSection.ifPresent(section -> this.sections.remove(section));
	}

	private void deleteUpSection(Optional<Section> upSection) {
		upSection.ifPresent(section -> this.sections.remove(section));
	}
}
