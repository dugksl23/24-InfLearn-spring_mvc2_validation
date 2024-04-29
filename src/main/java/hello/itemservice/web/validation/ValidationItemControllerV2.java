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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;


    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //매개변수 순서는, @ModelAttribute Item item, 다음인 BindingResult bindingResult 로 와야 한다.
        // bindingResult는 모델의 객체 바인딩 된 값에 대한 결과를 담고 있기에 다음에 두어야 한다.

        //검증 오류 결과를 보관
        Map<String, String> errors = new HashMap<>();

        // 필드 검증 로직
        if (!StringUtils.hasText(item.getItemName()) || Strings.isNullOrEmpty(item.getItemName())) {
            log.info("itemName : {}", item.getItemName());
            errors.put("itemName", "상품 이름은 필수입니다. ");
        }
        if (item.getPrice() == null || item.getPrice() < 999 || item.getPrice() > 1000000) {
            log.info("price : {}", item.getPrice());
            errors.put("price", "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
        }
        if (Optional.ofNullable(item.getQuantity()).isEmpty() || item.getQuantity() == 0 || item.getQuantity() > 9999) {
            log.info("quantity : {}", item.getQuantity());
            errors.put("quantity", "수량은 최대 9,999 까지 허용합니다.");
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            // 기존의 and　→ or 문법으로 바꿈
            int orderItemPrice = item.getPrice() * item.getQuantity();

            if (orderItemPrice < 10000) {
                errors.put("globalError", "최소 주문 금액은 10,000 이상입니다. 현재 주문 금액 : " + orderItemPrice);
            }
        }


        // 검증 실패시 다시 입력 Form 으로 Redirect
        if (!errors.isEmpty()) {
            log.info("errors : {}", errors.toString());
            model.addAttribute("errors", errors);
            return "validation/v2/addForm"; // 검증 실패시 입력 Page로 다시 보내기.
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

