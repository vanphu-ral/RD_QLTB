export enum PlanSupplieType {
    ANNUAL = 'ANNUAL',
    REPAIR_AND_MAINTENANCE = 'REPAIR_AND_MAINTENANCE'
}

export const PlanSupplieTypeLabel = new Map<string, string>([
    [PlanSupplieType.ANNUAL, 'Hàng năm'],
    [PlanSupplieType.REPAIR_AND_MAINTENANCE, 'Phục vụ sửa chữa lớn, đại tu, cải tạo thiết bị']
]);