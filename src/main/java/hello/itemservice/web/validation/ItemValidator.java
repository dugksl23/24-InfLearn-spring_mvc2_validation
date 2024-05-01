package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@Slf4j
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Item.class.isAssignableFrom(aClass);
        // aClass로 넘어온 Object의 참조값이 Item.class와 동일한지 확인하는 로직.
        // 항목 클래스는 a 클래스에서 할당 가능합니다.
        // -> Item = aClass
        // -> Item = subItem (자식 class/상속 관계)
    }

    @Override
    public void validate(Object o, Errors bindingResult) {
        log.info("들어와요?");
        Item item = (Item) o;
        //bindingResult는 Errors를 상속받았다. -> 부모는 자식을 담을 수 있다.

        //매개변수 순서는, @ModelAttribute Item item, 다음인 BindingResult bindingResult 로 와야 한다.
        // bindingResult는 모델의 객체 바인딩 된 값의 문제에  대한 결과를 담고 있기에 다음에 두어야 한다.
        // bindingResult에 의해서 controller가 정상 호출되며, 404 error 페이지로 client를 redirect 시키지 않는다.

        // 필드 검증 로직
        //ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");
        bindingResult.rejectValue("itemName", "required");


        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            //상품의 가격이 1000보다 작거나 같은 경우
            //상품의 가격이 1000000보다 큰 경우
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);

        }
        if (Optional.ofNullable(item.getQuantity()).isEmpty() || item.getQuantity() == 0 || item.getQuantity() > 9999) {
//            bindingResult.addError(new FieldError("item", "quantity", "Quantity must be between 0 and 9999"));
//            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "Price must be between 0 and 9999"));
            bindingResult.rejectValue("quantity", "max.item.quantity", new Object[]{0, 9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            log.info("가격 * 수량 필드 : {}, {}", item.getPrice(), item.getQuantity());
            // 기존의 and　→ or 문법으로 바꿈
            int orderItemPrice = item.getPrice() * item.getQuantity();

            if (orderItemPrice < 10000) {
                bindingResult.reject("orderItemPrice", new Object[]{10000, orderItemPrice, 10000}, null);
            }
        }
    }
}
