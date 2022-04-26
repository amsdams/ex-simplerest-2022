package food.web.rest.errors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import food.Crud1Application;
import web.rest.errors.ErrorConstants;
import web.rest.errors.ExceptionTranslator;

/**
 * Integration tests {@link ExceptionTranslator} controller advice.
 */

@AutoConfigureMockMvc
@SpringBootTest(classes = Crud1Application.class)
class ExceptionTranslatorIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testConcurrencyFailure() throws Exception {
		mockMvc.perform(get("/api/exception-translator-test/concurrency-failure")).andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.message").value(ErrorConstants.ERR_CONCURRENCY_FAILURE));
	}

	@Test
	void testMethodArgumentNotValid() throws Exception {
		mockMvc.perform(post("/api/exception-translator-test/method-argument").content("{}")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.message").value(ErrorConstants.ERR_VALIDATION))
				.andExpect(jsonPath("$.fieldErrors.[0].objectName").value("test"))
				.andExpect(jsonPath("$.fieldErrors.[0].field").value("test"))
				.andExpect(jsonPath("$.fieldErrors.[0].message").value("must not be null"));
	}

	@Test
	void testMissingServletRequestPartException() throws Exception {
		mockMvc.perform(get("/api/exception-translator-test/missing-servlet-request-part"))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.message").value("error.http.400"));
	}

	@Test
	void testMissingServletRequestParameterException() throws Exception {
		mockMvc.perform(get("/api/exception-translator-test/missing-servlet-request-parameter"))
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.message").value("error.http.400"));
	}

	@Test
	void testExceptionWithResponseStatus() throws Exception {
		mockMvc.perform(get("/api/exception-translator-test/response-status")).andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.message").value("error.http.400"))
				.andExpect(jsonPath("$.title").value("test response status"));
	}

	@Test
	void testInternalServerError() throws Exception {
		mockMvc.perform(get("/api/exception-translator-test/internal-server-error"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.message").value("error.http.500"))
				.andExpect(jsonPath("$.title").value("Internal Server Error"));
	}
}
