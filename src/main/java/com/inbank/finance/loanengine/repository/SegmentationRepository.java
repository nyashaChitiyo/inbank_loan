package com.inbank.finance.loanengine.repository;

import com.inbank.finance.loanengine.entity.Segmentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentationRepository extends JpaRepository<Segmentation,String> {
}
