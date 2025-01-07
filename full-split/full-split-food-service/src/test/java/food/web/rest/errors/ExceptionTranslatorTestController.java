package food.web.rest.errors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@RequestMapping("/api/exception-translator-test")
public class ExceptionTranslatorTestController {

	@GetMapping("/concurrency-failure")
	public void concurrencyFailure() {
		throw new ConcurrencyFailureException("test concurrency failure");
	}

	@PostMapping("/method-argument")
	public void methodArgument(@Valid @RequestBody TestDTO testDTO) {
	}

	@GetMapping("/missing-servlet-request-part")
	public void missingServletRequestPartException(@RequestPart String part) {
	}

	@GetMapping("/missing-servlet-request-parameter")
	public void missingServletRequestParameterException(@RequestParam String param) {
	}

	@GetMapping("/response-status")
	public void exceptionWithResponseStatus() {
		throw new TestResponseStatusException();
	}

	@GetMapping("/internal-server-error")
	public void internalServerError() {
		throw new RuntimeException();
	}
	
	@Data
	public static class TestDTO {

		@NotNull
		private String test;

		
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "test response status")
	@SuppressWarnings("serial")
	public static class TestResponseStatusException extends RuntimeException {
	}
}
