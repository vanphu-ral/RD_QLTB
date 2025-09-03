package io.qltb.qltb.repos;

import io.qltb.qltb.domain.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
}
