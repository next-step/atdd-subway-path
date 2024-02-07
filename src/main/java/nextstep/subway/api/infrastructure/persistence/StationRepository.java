package nextstep.subway.api.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.api.domain.model.entity.Station;

public interface StationRepository extends JpaRepository<Station, Long> {

	/**
	 * 상행과 하행선을 각각 조회해야 하는 상황에서 조회 IO를 2회 -> 1회로 줄일 수 있다.
	 * IN 쿼리가 pk 기반의 복잡하기 때문에 실제 상황에서 pk를 2번 조회하는 게 나을지, IN 쿼리로 1회 조회하는 게 나을지는
	 * 테스트가 필요할 듯 하다.
	 * @param ids
	 * @return
	 */
	List<Station> findByIdIn(List<Long> ids);
}