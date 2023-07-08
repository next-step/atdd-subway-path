package subway.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StationLineCreateRequest {
	private String name;
	private String color;
	private Long upStationId;
	private Long downStationId;
	private BigDecimal distance;
}
