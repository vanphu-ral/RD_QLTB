package io.rd.qltb.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Bo loc dung chung cho API dashboard (tong hop + chi tiet).
 * Query param phan trang: page, size, sort (Spring Pageable).
 */
@Getter
@Setter
public class DashboardFilter {

    /** Loc theo nhieu ngành (OR). null/empty va khong co branchId/branchName → tong tat ca ngành. */
    private List<Long> branchIds;
    /** Ten nhieu ngành — khop chinh xac, khong phan biet hoa thuong (gop vao branchIds). */
    private List<String> branchNames;
    /** Don ngành (tuong thich cu); gop chung voi branchIds. */
    private Long branchId;
    /** Don ten ngành (tuong thich cu). */
    private String branchName;
    private Long teamId;
    private Long groupId;
    /** Năm lọc KH bảo trì / lượt thực hiện; null → năm hiện tại. */
    private Integer year;
    /** Tháng lọc KH bảo trì tháng (1–12); null → tháng hiện tại. */
    private Integer month;
    private String deviceName;
    /** OVERDUE | UPCOMING | ALL — dùng cho API đến hạn bảo trì. */
    private String maintenanceDueType;
    /** Bật true de API /data tra kem thoi gian tung query (ms) trong DashboardDTO. */
    private Boolean includeTiming;

}