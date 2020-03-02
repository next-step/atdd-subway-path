package atdd.path.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode(of = {"id", "name"})
public class Item {
    private Long id;
    private String name;

    public static Item of(Long id, String name) {
        return Item.builder().id(id).name(name).build();
    }
}
