package food.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * A Food.
 */
@Entity
@Table(name = "food")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class Food implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
	@SequenceGenerator(name = "sequenceGenerator")
	@Column(name = "id")
	private Long id;

	@NotNull
	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "published")
	private Boolean published;

}
