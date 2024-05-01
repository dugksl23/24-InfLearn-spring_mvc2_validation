package hello.itemservice.domain.item;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class ItemValidationTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    void BeanValidationTest() {

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Item item = new Item();
        item.setPrice(0);
        item.setQuantity(0);

        Set<ConstraintViolation<Item>> validate = validator.validate(item);
        validate.stream().forEach(violation -> {
            log.info("violation : {}", violation.toString());
            log.info("violation field : {}", violation.getPropertyPath());
            log.info("violation value : {}", violation.getInvalidValue());
            log.info("violation message : {}", violation.getMessage());
        });


    }

}