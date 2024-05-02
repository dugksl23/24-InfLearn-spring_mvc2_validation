package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity > 10000", message = "가격과 * 수량의 합은 10,000원 이상이어야 합니다.")
public class Item {

    @NotNull(groups = UpdateCheck.class)
    private Long id;

    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class, })
    private String itemName;
    @NotNull(groups = {SaveCheck.class, UpdateCheck.class, })
    @Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
    private Integer price;

    @NotNull(message = "입력해주세요.", groups = {SaveCheck.class, UpdateCheck.class, })
    @Max(value = 9999, groups = {SaveCheck.class}) //등록시 체크하고, 수정시 포함 X
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
