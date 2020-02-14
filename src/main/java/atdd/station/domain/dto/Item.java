package atdd.station.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Item {
    private Long id;
    private String name;

    public static Item of(Long id, String name) {
        return Item.builder().id(id).name(name).build();
    }
}
