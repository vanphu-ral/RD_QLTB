# -*- coding: utf-8 -*-
"""Generate docs/API_Dashboard_QLTB.xlsx — run: python scripts/gen_dashboard_api_excel.py"""
from pathlib import Path

from openpyxl import Workbook
from openpyxl.styles import Alignment, Font, PatternFill
from openpyxl.utils import get_column_letter

OUT = Path(__file__).resolve().parent.parent / "docs" / "API_Dashboard_QLTB.xlsx"

HEADER_FILL = PatternFill("solid", fgColor="548235")
HEADER_FONT = Font(bold=True, color="FFFFFF", size=11)
SUBHEADER_FILL = PatternFill("solid", fgColor="D9E1F2")
WRAP = Alignment(wrap_text=True, vertical="top")

AUTH = "Phải đăng nhập. POST gửi JSON trong body."

FILTER_BODY = """Ví dụ:
{
  "branchIds": [2],
  "year": 2025,
  "month": 11
}
• Không chọn ngành → số liệu cả nhà máy.
• Không gửi year/month → tháng hiện tại."""

STATUS = "Mới"
BASE = "/api/dashboard"
BIZ = "Dashboard QLTB"

API_ROWS = [
    ("GET", f"{BASE}/data", "Xem các con số trên màn hình chính (không ra danh sách dài).", "Có thể thêm ?branchIds=2&year=...", "Body JSON (tùy chọn)", "Sheet «1. Tổng hợp /data»"),
    ("POST", f"{BASE}/data", "Giống GET nhưng gửi bộ lọc trong body.", "", FILTER_BODY, "Sheet «1. Tổng hợp /data»"),
    ("POST", f"{BASE}/working-devices", "Bấm vào ô Mục 1 → xem danh sách TB đang dùng.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/monthly-maintenance-plan", "Danh sách TB nằm trong kế hoạch bảo trì tháng.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/yearly-maintenance-plan", "Danh sách TB trong kế hoạch bảo trì cả năm.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/maintained-devices", "TB đã làm xong bảo dưỡng.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/maintenance-executions", "Từng lần đã thực hiện bảo trì.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/accepted-maintenance", "Biên bản nghiệm thu bảo trì đã duyệt.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/due-maintenance", "TB sắp đến hạn hoặc quá hạn bảo dưỡng.", "page, size", FILTER_BODY + '\nmaintenanceDueType: OVERDUE | UPCOMING | ALL', "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/faulty-devices", "TB đang ở trạng thái hỏng (theo quy trình duyệt).", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
    ("POST", f"{BASE}/critical-incidents", "Biên bản báo cáo sự cố.", "page, size", FILTER_BODY, "Sheet «2. Chi tiết từng API»"),
]

# Cột: Tên trường, Giải thích ngắn
FILTER_FIELDS = [
    ("branchIds", "Chọn một hoặc nhiều ngành (gửi id). Không gửi = lấy tất cả ngành."),
    ("branchNames", "Chọn ngành theo đúng tên (gửi danh sách tên)."),
    ("branchId / branchName", "Chọn một ngành (cách cũ, vẫn dùng được)."),
    ("teamId", "Chỉ xem một tổ (tùy chọn)."),
    ("groupId", "Chỉ xem một nhóm thiết bị (tùy chọn)."),
    ("year", "Năm xem kế hoạch / số lần bảo trì. Không gửi = năm nay."),
    ("month", "Tháng xem kế hoạch bảo trì. Không gửi = tháng nay."),
    ("deviceName", "Gõ vài chữ tên TB để tìm (chỉ API danh sách chi tiết)."),
    ("maintenanceDueType", "Lọc đến hạn: OVERDUE = quá hạn, UPCOMING = chưa tới hạn, ALL = tất cả."),
    ("includeTiming", "true = trả thêm thời gian chạy query (để kiểm tra hiệu năng)."),
]

# API POST/GET /data
SUMMARY_FIELDS = [
    ("totalDevices", "Tổng số thiết bị (chưa bị xóa khỏi hệ thống)."),
    ("totalWorkingDevices", "Số TB đang sử dụng — tương ứng Mục 1 trên màn hình."),
    ("totalDevicesInMaintenance", "Số TB nằm trong kế hoạch bảo trì của tháng đang chọn — Mục 2."),
    ("totalDevicesMaintained", "Số TB nằm trong kế hoạch bảo trì của cả năm đang chọn."),
    ("totalMaintained", "Số TB đã bảo dưỡng xong (hoàn thành trong kế hoạch)."),
    ("maintenanceExecutionCount", "Có bao nhiêu lần đã làm bảo trì trong năm đang chọn — Mục 3."),
    ("acceptedPeriodicMaintenanceDeviceCount", "Bao nhiêu TB đã nghiệm thu bảo trì định kỳ — Mục 5."),
    ("dueMaintenanceDeviceCount", "Bao nhiêu TB đang đến hạn hoặc quá hạn bảo dưỡng — Mục 5."),
    ("faultyDeviceCount", "Bao nhiêu TB đang ở trạng thái hỏng — Mục 6."),
    ("criticalIncidentReportCount", "Bao nhiêu biên bản sự cố — Mục 7."),
    ("year", "Năm hệ thống đang dùng để tính (hiện lại cho FE biết)."),
    ("month", "Tháng hệ thống đang dùng để tính."),
    ("appliedBranchIds", "Danh sách id ngành đã lọc (nếu có chọn ngành)."),
    ("queryTimingsMs", "Thời gian từng bước đếm (chỉ khi bật includeTiming)."),
    ("totalQueryTimeMs", "Tổng thời gian đếm số liệu (mill giây)."),
]

# Mỗi API chi tiết: (Tên API, [(trường, giải thích), ...])
DETAIL_API_FIELDS = [
    (
        "Chung — mọi API danh sách đều bọc trong Page",
        [
            ("content", "Mảng chứa từng dòng chi tiết."),
            ("totalElements", "Tổng cộng bao nhiêu dòng (không phải chỉ 1 trang)."),
            ("number", "Đang xem trang thứ mấy (bắt đầu từ 0)."),
            ("size", "Mỗi trang tối đa bao nhiêu dòng."),
            ("totalPages", "Tổng số trang."),
        ],
    ),
    (
        "/working-devices — TB đang dùng (Mục 1)",
        [
            ("id", "Mã số nội bộ."),
            ("code", "Mã thiết bị."),
            ("name", "Tên thiết bị."),
            ("state", "1 = đang sử dụng."),
            ("branch / team / group", "Ngành, tổ, nhóm của TB."),
        ],
    ),
    (
        "/monthly-maintenance-plan, /yearly-maintenance-plan, /maintained-devices",
        [
            ("deviceCode, deviceName", "Mã và tên TB."),
            ("planName", "Tên kế hoạch bảo trì."),
            ("estimatedTime", "Ngày dự kiến bảo trì."),
            ("planDetailStatus", "Trạng thái dòng trong kế hoạch."),
            ("branchName, teamName, lineName", "TB thuộc ngành / tổ / line nào."),
        ],
    ),
    (
        "/maintenance-executions — Mục 3",
        [
            ("deviceCode, deviceName", "TB được bảo trì."),
            ("planName", "Thuộc kế hoạch nào."),
            ("dateTest", "Ngày thực hiện bảo trì."),
            ("resultStatus", "Kết quả lần bảo trì đó."),
        ],
    ),
    (
        "/due-maintenance — đến hạn bảo dưỡng",
        [
            ("deviceName", "Tên TB."),
            ("nextTest", "Ngày cần bảo trì tiếp theo (tính theo chu kỳ)."),
            ("daysDiff", "Còn bao nhiêu ngày (âm = đã quá hạn)."),
            ("status", "OVERDUE = quá hạn, UPCOMING = chưa tới, NO_DATA = chưa có lịch sử."),
            ("branchName", "Ngành."),
        ],
    ),
    (
        "/accepted-maintenance — nghiệm thu",
        [
            ("code, name", "Mã và tên biên bản nghiệm thu."),
            ("device", "Thiết bị được nghiệm thu."),
            ("timeAcceptance", "Thời điểm nghiệm thu."),
        ],
    ),
    (
        "/faulty-devices — Mục 6",
        [
            ("code, name", "TB bị hỏng."),
            ("status", "Trạng thái xử lý trên hệ thống (khác với state đang dùng)."),
        ],
    ),
    (
        "/critical-incidents — Mục 7",
        [
            ("id, code", "Biên bản sự cố."),
            ("device", "TB liên quan."),
        ],
    ),
]

SQL_NOTES = [
    ("Đếm TB một ngành", "WHERE branch_id = 2 AND (status IS NULL OR status <> 10)"),
    ("Khớp số Mục 1", "Thêm điều kiện state = 1"),
    ("Lỗi thường gặp", "Dùng status NOT IN (10) sẽ bỏ sót bản ghi status đang trống (NULL)"),
]


def write_header_row(ws, headers, row=1):
    for col, title in enumerate(headers, start=1):
        cell = ws.cell(row=row, column=col, value=title)
        cell.fill = HEADER_FILL
        cell.font = HEADER_FONT
        cell.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)


def set_col_widths(ws, widths):
    for i, w in enumerate(widths, start=1):
        ws.column_dimensions[get_column_letter(i)].width = w


def sheet_api_list(wb):
    ws = wb.active
    ws.title = "0. Danh sach API"
    headers = ["Type", "Method", "URL", "Dùng để làm gì", "Query", "Body", "Xem giai thich o sheet", "TT"]
    write_header_row(ws, headers)
    for r, row in enumerate(API_ROWS, start=2):
        vals = ["REST API", row[0], row[1], row[2], row[3], row[4], row[5], STATUS]
        for c, val in enumerate(vals, start=1):
            ws.cell(row=r, column=c, value=val).alignment = WRAP
    ws.cell(row=r + 2, column=1, value=AUTH).alignment = WRAP
    set_col_widths(ws, [10, 8, 34, 40, 24, 38, 28, 8])


def sheet_filter(wb):
    ws = wb.create_sheet("Request - bo loc")
    write_header_row(ws, ["Tên gửi lên (JSON)", "Hiểu đơn giản"])
    for r, row in enumerate(FILTER_FIELDS, start=2):
        ws.cell(row=r, column=1, value=row[0]).alignment = WRAP
        ws.cell(row=r, column=2, value=row[1]).alignment = WRAP
    set_col_widths(ws, [28, 75])


def sheet_summary_response(wb):
    ws = wb.create_sheet("1. Tong hop -data")
    write_header_row(ws, ["Trường trong JSON", "Nó là gì? (dễ hiểu)"])
    ws.cell(row=2, column=1, value="API: GET hoặc POST /api/dashboard/data").alignment = WRAP
    ws.merge_cells("A2:B2")
    ws.cell(row=2, column=1).fill = SUBHEADER_FILL
    for r, row in enumerate(SUMMARY_FIELDS, start=3):
        ws.cell(row=r, column=1, value=row[0]).alignment = WRAP
        ws.cell(row=r, column=2, value=row[1]).alignment = WRAP
    set_col_widths(ws, [38, 70])


def sheet_detail_response(wb):
    ws = wb.create_sheet("2. Chi tiet tung API")
    write_header_row(ws, ["API", "Tên trường", "Nó là gì?"])
    r = 2
    for api_name, fields in DETAIL_API_FIELDS:
        ws.cell(row=r, column=1, value=api_name).fill = SUBHEADER_FILL
        ws.cell(row=r, column=1).alignment = WRAP
        ws.merge_cells(start_row=r, start_column=1, end_row=r, end_column=3)
        r += 1
        for fname, desc in fields:
            ws.cell(row=r, column=1, value="").alignment = WRAP
            ws.cell(row=r, column=2, value=fname).alignment = WRAP
            ws.cell(row=r, column=3, value=desc).alignment = WRAP
            r += 1
        r += 1
    set_col_widths(ws, [42, 32, 55])


def sheet_sql(wb):
    ws = wb.create_sheet("3. Kiem tra bang SQL")
    write_header_row(ws, ["Việc cần làm", "Ghi chú"])
    for r, row in enumerate(SQL_NOTES, start=2):
        ws.cell(row=r, column=1, value=row[0]).alignment = WRAP
        ws.cell(row=r, column=2, value=row[1]).alignment = WRAP
    set_col_widths(ws, [30, 70])


def main():
    OUT.parent.mkdir(parents=True, exist_ok=True)
    wb = Workbook()
    sheet_api_list(wb)
    sheet_filter(wb)
    sheet_summary_response(wb)
    sheet_detail_response(wb)
    sheet_sql(wb)
    wb.save(OUT)
    print(f"Written: {OUT}")


if __name__ == "__main__":
    main()