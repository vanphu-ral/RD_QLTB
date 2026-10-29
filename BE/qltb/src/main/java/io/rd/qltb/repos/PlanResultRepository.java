package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PlanResultRepository extends JpaRepository<PlanResult, Long> {
    List<PlanResult> findByPlanDetailId(Long planDetailId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM plan_results pr WHERE pr.plan_detail_id = ?1 ",nativeQuery = true)
    void deleteAllByPlanDetailId(Long planDetailId);
    @Query(value = "SELECT * FROM plan_results pr WHERE pr.plan_detail_id = ?1 and date_test like ?2 ;",nativeQuery = true)
    List<PlanResult> findByPlanDetailIdAndDateTestLike(Long planDetailId, String dateTestPattern);
    @Query(value = "SELECT pr.* FROM plan_results pr \n" +
            "INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id \n" +
            "INNER JOIN devices d ON d.id = pd.device_id \n" +
            "INNER JOIN plans p ON pd.plan_id = p.id \n" +
            "WHERE d.id = 10 AND p.plan_type_id = 5 \n" +
            "ORDER BY pr.id DESC \n" +
            "LIMIT 1 ;" ,nativeQuery = true)
    PlanResult findLatestPlanResultByDeviceIdAndPlanTypeMaintenance(Long deviceId);

    @Query(value = "SELECT " +
            "d.id AS device_id, " +
            "d.code AS device_code, " +
            "d.name AS device_name, " +
            "d.qr_code AS qr_code, " +
            "l.name AS line_name, " +
            "t.name AS team_name, " +
            "b.name AS branch_name, " +
            "MAX(pr.id) AS plan_result_id, " +
            "MAX(pr.status) AS status, " +
            "MAX(COALESCE(prd.updated_at, prd.created_at, pr.updated_at, pr.created_at)) AS last_checked_time " +
            "FROM plan_results pr " +
            "JOIN plan_details pd ON pr.plan_detail_id = pd.id " +
            "JOIN devices d ON pd.device_id = d.id " +
            "LEFT JOIN `lines` l ON d.line_id = l.id " +
            "LEFT JOIN teams t ON d.team_id = t.id " +
            "LEFT JOIN branches b ON d.branch_id = b.id " +
            "LEFT JOIN plan_result_details prd ON prd.plan_result_id = pr.id " +
            "WHERE (FIND_IN_SET(:username, pr.user_test) > 0 OR LOWER(pr.user_test) LIKE CONCAT('%', LOWER(:username), '%') OR LOWER(pr.created_by) = LOWER(:username) OR LOWER(pr.updated_by) = LOWER(:username) OR LOWER(prd.created_by) = LOWER(:username) OR LOWER(prd.updated_by) = LOWER(:username)) " +
            "  AND (DATE(pr.date_test) = :today OR DATE(pr.updated_at) = :today OR DATE(prd.created_at) = :today OR DATE(prd.updated_at) = :today) " +
            "GROUP BY d.id, d.code, d.name, d.qr_code, l.name, t.name, b.name " +
            "ORDER BY last_checked_time DESC", nativeQuery = true)
    List<Object[]> findCheckedDevicesToday(@org.springframework.data.repository.query.Param("username") String username,
                                          @org.springframework.data.repository.query.Param("today") String today);
}
