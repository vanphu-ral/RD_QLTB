package io.rd.qltb.repos;

import io.rd.qltb.domain.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
}
