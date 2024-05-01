package hello.itemservice;

import hello.itemservice.web.validation.ItemValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ItemServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

//	@Override
//	public Validator getValidator() {
//		return new ItemValidator();
//
//	}
	// 글로벌 설정과 컨트롤러 설정 둘다 적용할 경우
	// 해당 검증이 두차례 이뤄진다.
}
