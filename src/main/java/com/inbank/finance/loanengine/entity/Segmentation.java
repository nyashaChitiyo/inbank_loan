package com.inbank.finance.loanengine.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "segmentation")
public class Segmentation {

    @Id
    private String segmentId;
    private String segmentName;
    private int creditModifier;
}
