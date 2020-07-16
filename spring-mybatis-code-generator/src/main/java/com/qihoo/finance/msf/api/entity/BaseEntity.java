package com.qihoo.finance.msf.api.entity;

import com.qihoo.finance.msf.api.domain.BaseDomain;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity extends BaseDomain {
    private static final long serialVersionUID = 7952514626314547466L;

/*    @Id
    @GeneratedValue(generator = "JDBC")*/
    private Long id;

    private Date dateCreated;

    private String createdBy;

    private Date dateUpdated;

    private String updatedBy;

/*    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }*/
}