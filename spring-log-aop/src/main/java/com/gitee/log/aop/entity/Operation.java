package com.gitee.log.aop.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Operation {
	private String id;

	private Long objectId;

	private String objectClass;

	private String operator;

	private Date operateTime;

	private OperationType actionType;

	private List<ChangeItem> changes = new ArrayList<>();
}
