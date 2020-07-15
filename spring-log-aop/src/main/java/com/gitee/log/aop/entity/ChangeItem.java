package com.gitee.log.aop.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangeItem {
	private String field;

	private String fieldShowName;

	private String oldValue;

	private String newValue;
}
