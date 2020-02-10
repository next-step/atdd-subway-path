package atdd.station.service;

import atdd.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StationServiceTest {

    private StationService stationService;
    private StationRepository stationRepository;

    @BeforeEach
    void setup() {
        stationRepository = mock(StationRepository.class);
        stationService = new StationService(stationRepository);
    }

    @DisplayName("create - name 이 null 이거나 비어있으면 에러")
    @ParameterizedTest
    @NullAndEmptySource
    void create(String name) {
        assertThatThrownBy(() -> stationService.create(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name 은 필수 입니다.");
    }

    @DisplayName("조회 결과가 없으면 에러")
    @Test
    void getStation() {
        final String name = "역삼역";

        when(stationRepository.findByName(eq(name))).thenThrow(new EntityNotFoundException());

        assertThatThrownBy(() -> stationService.getStation(name))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("getStation - name 이 null 이거나 비어있으면 에러")
    @ParameterizedTest
    @NullAndEmptySource
    void getStation_empty_name(String name) {
        assertThatThrownBy(() -> stationService.create(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name 은 필수 입니다.");
    }

    @DisplayName("delete - name 이 null 이거나 비어있으면 에러")
    @ParameterizedTest
    @NullAndEmptySource
    void delete_empty_name(String name) {
        assertThatThrownBy(() -> stationService.delete(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name 은 필수 입니다.");
    }

    @DisplayName("delete - 삭제된 내용이 없으면 에러")
    @Test
    void delete_no_result() {
        final String name = "name!!";

        when(stationRepository.deleteByName(name)).thenReturn(0);


        assertThatThrownBy(() -> stationService.delete(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 name 입니다. name = [name!!]");
    }

}