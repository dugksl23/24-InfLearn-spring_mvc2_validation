package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Set;

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

        Assertions.assertThat(strings).containsExactly("required.item.itemName", "required.itemName", "required.java.lang.String", "required");
    }

    @Test
    public void messageCodeResolverFieldForOrderItemPrice() {
        String[] strings = messageCodesResolver.resolveMessageCodes("orderItemPrice", "item", "", String.class);
        System.out.println(Arrays.toString(strings));


        //Assertions.assertThat(strings).containsExactly("required.item.itemName", "required.itemName", "required.java.lang.String", "required");
    }

    @Test
    void BeanValidationErrorCodeTest(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // 검사할 객체 생성
        Item item = new Item();

        // 유효성 검사 실행
        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        // 결과 출력
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("Property: " + violation.getPropertyPath());
            System.out.println("Message: " + violation.getMessage());
            System.out.println("Constraint: " + violation.getConstraintDescriptor());
            System.out.println("Constraint: " + violation.getConstraintDescriptor());
            String[] strings = messageCodesResolver.resolveMessageCodes("Range", "item", "itemName", Integer.class);
            System.out.println(Arrays.toString(strings));

            System.out.println("---------------------------");
        }
    }



}
