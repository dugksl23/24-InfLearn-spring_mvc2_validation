package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import java.util.Arrays;

public class MessageCodesResolverTest {

    private final MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    @Test
    public void messageCodesResolverObject() {
        String[] strings = messageCodesResolver.resolveMessageCodes("required", "item");
        System.out.println(Arrays.toString(strings));

        Assertions.assertThat(strings).containsExactly("required.item", "required");


    }

    @Test
    public void messageCodeResolverField() {
        String[] strings = messageCodesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        System.out.println(Arrays.toString(strings));
        //bindingResult.rejectValue("itemName", "required"); // field name, errorCode
        //-> 내부적으로 하기 코드의 흐름을 가진다.
        // new FieldError("item", "itemName", null, "false", messageCodeResolver, null, null);

        Assertions.assertThat(strings).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
