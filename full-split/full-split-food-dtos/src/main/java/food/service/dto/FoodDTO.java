package food.service.dto;


import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.amsdams.ex.domain.Food} entity.
 */

public class FoodDTO implements Serializable {

	private Long id;

	@NotNull
	private String title;

	private String description;

	private Boolean published;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FoodDTO foodDTO)) {
			return false;
		}

		if (this.id == null) {
			return false;
		}
		return Objects.equals(this.id, foodDTO.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "FoodDTO{" + "id=" + getId() + ", title='" + getTitle() + "'" + ", description='" + getDescription()
				+ "'" + ", published='" + getPublished() + "'" + "}";
	}
}
