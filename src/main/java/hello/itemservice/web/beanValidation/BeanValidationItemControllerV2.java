package hello.itemservice.web.beanValidation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/bean/validation/v4/items")
@RequiredArgsConstructor
@Slf4j
public class BeanValidationItemControllerV2 {

    private final ItemRepository itemRepository;

//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.addValidators(new ItemValidator());
//    }


    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "/beanValidation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "/beanValidation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "/beanValidation/v4/addForm";
    }

    //    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 1. 특정 필드가 아닌 복합 룰 검증와 같은
        //    글로벌 에러는 별도 자바 코드를 작성한다.
        // 2. FieldError 는 Bean Validation
        // 3. Object는 Error 는 Java Code 로 해결
        validateOrderItemTotalPrice(item, bindingResult);

        // 2. 바인딩 Error에 대한 분기점 처리는 비지니스 로직 수행 이후에 가능하다.
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "beanValidation/v4/addForm";
        }
        // 3. 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/bean/validation/v4/items/{itemId}";

    }

    @PostMapping("/add")
    public String addItem2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        validateOrderItemTotalPrice(item, bindingResult);

        // 2. 바인딩 Error에 대한 분기점 처리는 비지니스 로직 수행 이후에 가능하다.
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "beanValidation/v4/addForm";
        }
        // 3. 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/bean/validation/v4/items/{itemId}";

    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "/beanValidation/v4/editForm";
    }

//    @PostMapping("/{itemId}/edit")
    public String edit2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        validateOrderItemTotalPrice(item, bindingResult);
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "/beanValidation/v4/editForm";
        }
        itemRepository.update(itemId, item);
        return "redirect:/bean/validation/v4/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Valid @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        validateOrderItemTotalPrice(item, bindingResult);
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "/beanValidation/v4/editForm";
        }
        itemRepository.update(itemId, item);
        return "redirect:/bean/validation/v4/items/{itemId}";
    }


    private static void validateOrderItemTotalPrice(Item item, BindingResult bindingResult) {
        if (item.getPrice() != null && item.getQuantity() != null) {
            // 기존의 and　→ or 문법으로 바꿈
            int orderItemPrice = item.getPrice() * item.getQuantity();

            if (orderItemPrice < 10000) {
                bindingResult.reject("orderItemPrice", new Object[]{10000, orderItemPrice, 10000}, null);

            }
        }
    }

}

