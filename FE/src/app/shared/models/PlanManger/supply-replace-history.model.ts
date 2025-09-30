export class SupplyReplacementHistory {
    id?: number;
    quantityOld?: number;
    quantityChange?: number;
    reason?: string;
    plan?: number;
    planResult?: number;
    createdBy?: string;
    createdAt?: Date;
    oldSupplyDetail?: any;
    newSupplyDetail?: any;
}