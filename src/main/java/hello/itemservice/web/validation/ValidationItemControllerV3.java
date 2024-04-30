package hello.itemservice.web.validation;

import com.google.common.base.Strings;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/validation/V3/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;


    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        log.info("Model Target : {}", bindingResult.getTarget());

        //매개변수 순서는, @ModelAttribute Item item, 다음인 BindingResult bindingResult 로 와야 한다.
        // bindingResult는 모델의 객체 바인딩 된 값의 문제에  대한 결과를 담고 있기에 다음에 두어야 한다.
        // bindingResult에 의해서 controller가 정상 호출되며, 404 error 페이지로 client를 redirect 시키지 않는다.

        // 필드 검증 로직
        if (!StringUtils.hasText(item.getItemName()) || Strings.isNullOrEmpty(item.getItemName())) {
            // 3번째 파라미터는 reject Value를 포함시켜서 사용자 화면에 값을 유지키기 위함.
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
//            bindingResult.addError(new FieldError("item", "itemName", "Item name is required"));
            // objectName 은 valid 의 대상이되는 객체명 이름
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            //상품의 가격이 1000보다 작거나 같은 경우
            //상품의 가격이 1000000보다 큰 경우
//            bindingResult.addError(new FieldError("item", "price", "Price must be between 0 and 9999"));
//            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "Price must be between 0 and 9999"));
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));

        }
        if (Optional.ofNullable(item.getQuantity()).isEmpty() || item.getQuantity() == 0 || item.getQuantity() > 9999) {
//            bindingResult.addError(new FieldError("item", "quantity", "Quantity must be between 0 and 9999"));
//            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "Price must be between 0 and 9999"));
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{0, 9999}, null));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            // 기존의 and　→ or 문법으로 바꿈
            int orderItemPrice = item.getPrice() * item.getQuantity();

            if (orderItemPrice < 10000) {
                // field error가 아닌 경우에는, object error로 진행한다.
//                bindingResult.addError(new ObjectError("item", "globalError 최소 주문 금액은 10,000 이상입니다. 현재 주문 금액 : " + orderItemPrice));
//                bindingResult.addError(new ObjectError("item", null, null, "globalError 최소 주문 금액은 10,000 이상입니다. 현재 주문 금액 : " + orderItemPrice));
                bindingResult.addError(new ObjectError("item", new String[]{"orderItemPrice"}, new Object[]{10000, orderItemPrice, 10000}, null));

            }
        }

        // 검증 실패시 다시 입력 Form 으로 Redirect
        if (bindingResult.hasErrors()) {
            log.info("errors : {}", bindingResult);
            //model.addAttribute("errors", bindingResult);
            //　bindingResult는 model에 담지 않아도 된다.
            // 자동으로 myView.render(model, bindingResult) 함꼐 넘어간다.
            return "validation/v3/addForm";
            // 검증 실패시 입력 Page로 다시 보내기.
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v3/items/{itemId}";

    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

