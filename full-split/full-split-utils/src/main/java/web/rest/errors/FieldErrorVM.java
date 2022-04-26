package web.rest.errors;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldErrorVM implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String objectName;

	private final String field;

	private final String message;

}
