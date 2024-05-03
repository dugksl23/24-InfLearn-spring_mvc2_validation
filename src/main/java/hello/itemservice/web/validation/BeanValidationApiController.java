package hello.itemservice.web.validation;


import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.web.dto.ItemSaveDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bean/validation/v1")
@Slf4j
public class BeanValidationApiController {

    private final ItemRepository itemRepository;

    @PostMapping("/saveItem")
    public Object addItem(@RequestBody @Valid ItemSaveDto item, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.info("errors : {}", bindingResult.getAllErrors().toString());
            return bindingResult.getAllErrors();
        }

        Item itemEntity = item.createItemEntity();
        Item save = itemRepository.save(itemEntity);
        log.info("item saved : {}", save.toString());

        return new ApiResultResponse(1, item);
    }

}
