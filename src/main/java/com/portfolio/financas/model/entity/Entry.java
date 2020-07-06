package com.portfolio.financas.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import com.portfolio.financas.model.enums.EntryStatus;
import com.portfolio.financas.model.enums.EntryType;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Entity
@Table(name = "entry", schema = "finances")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Entry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "description")
	private String description;
	
	@Column(name = "month")
	private Integer month;
	
	@Column(name = "year")
	private Integer year;
	
	@ManyToOne
	@JoinColumn(name = "id_user")
	private User user;
	
	@Column(name = "value")
	private BigDecimal value;

	@Column(name = "registry_date")
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate registryDate;

	@Column(name = "type")
	@Enumerated(value = EnumType.STRING)
	private EntryType type;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private EntryStatus status;
}