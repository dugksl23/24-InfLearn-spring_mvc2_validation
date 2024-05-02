package hello.itemservice.web.beanValidation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.dto.ItemDto;
import hello.itemservice.web.dto.ItemSaveDto;
import hello.itemservice.web.dto.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<ItemDto> itemList = items.stream().map(item -> {
            return new ItemDto(item);
        }).collect(Collectors.toList());
        model.addAttribute("items", itemList);
        return "/beanValidation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        ItemDto itemDto = new ItemDto(item);
        model.addAttribute("itemDto", itemDto);
        return "/beanValidation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        //model.addAttribute("item", new Item());
        model.addAttribute("itemSaveDto", new ItemSaveDto());
        return "/beanValidation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("itemSaveDto") ItemSaveDto item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // @ModelAttribute("item") 는
        // 1. key 값을 정해주지 않으면 오브젝트의 이름을 소문자로 바꾸고, item 을 value 로 지정한다.
        // 2. 실무에서는 view 전달되는 객체명에 맞춰서 key 값을 설정!!

        // 1. 특정 필드가 아닌 복합 룰 검증와 같은
        //    글로벌 에러는 별도 자바 코드를 작성한다.
        // 2. FieldError 는 Bean Validation
        // 3. Object는 Error 는 Java Code 로 해결
        validateOrderItemTotalPrice(item.getPrice(), item.getQuantity(), bindingResult);

        // 2. 바인딩 Error에 대한 분기점 처리는 비지니스 로직 수행 이후에 가능하다.
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "beanValidation/v4/addForm";
        }

        // 해당 command 에 맞는 로직은 캡슐화하자!
        // domain 은 비지니스 로직만을 캡슐화해야 한다 -> Domain Driven Design
        Item itemEntity = item.createItemEntity();

        // 3. 성공 로직
        Item savedItem = itemRepository.save(itemEntity);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/bean/validation/v4/items/{itemId}";

    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        log.info("itemId: {}", itemId);
        Item item = itemRepository.findById(itemId);
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(item);
        model.addAttribute("itemUpdateDto", itemUpdateDto);
        return "beanValidation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") Long itemId, @Valid @ModelAttribute("itemUpdateDto") ItemUpdateDto item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        log.info("editing itemId: {}", itemId);
        validateOrderItemTotalPrice(item.getPrice(), item.getQuantity(), bindingResult);
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.toString());
            return "/beanValidation/v4/editForm";
        }

        Item itemEntity = item.createItemEntity();

        itemRepository.update(itemId, itemEntity);
        return "redirect:/bean/validation/v4/items/{itemId}";
    }


    private static void validateOrderItemTotalPrice(Integer price, Integer quantity, BindingResult bindingResult) {
        if (price != null && quantity != null) {
            // 기존의 and　→ or 문법으로 바꿈
            int orderItemPrice = price * quantity;

            if (orderItemPrice < 10000) {
                bindingResult.reject("orderItemPrice", new Object[]{10000, orderItemPrice, 10000}, null);

            }
        }
    }

}

