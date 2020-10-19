package com.baomidou.springboot.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户表
 */
@Setter
@Getter
public class User {

	@NotNull
	private String name;

	private String otherName;

	@Max(message = "类型范围在1-5之间", value = 5)
	@Min(message = "类型范围在1-5之间", value = 1)
	private Integer testType;

	private Date testDate;

	private LocalDateTime localDateTime;
	private LocalDate localDate;

	private Long role;

	private String message;

}
