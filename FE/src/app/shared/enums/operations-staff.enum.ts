export enum OperationsStaff {
    CNVH = 'CNVH', // Công nhân vận hành
    KTV = 'KTV', // Kỹ thuật viên
    TP = 'TP', // Tổ phó
    NVQLCL = 'NVQLCL', // Nhân viên quản lý chất lượng
    NVHT = 'NVHT' // Nhân viên hệ thống
}

export const OperationsStaffLabel = new Map<string, string>([
    [OperationsStaff.CNVH, 'Công nhân vận hành'],
    [OperationsStaff.KTV, 'Kỹ thuật viên'],
    [OperationsStaff.TP, 'Tổ phó'],
    [OperationsStaff.NVQLCL, 'Nhân viên quản lý chất lượng'],
    [OperationsStaff.NVHT, 'Nhân viên hệ thống']
]);
