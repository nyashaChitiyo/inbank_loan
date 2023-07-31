package com.inbank.finance.loanengine.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private String personalCode;
    private String name;
    private String segmentId;
}
