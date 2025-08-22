export interface Department {
    id: number;
    name?: string;
    code?: string;
    description?: string;
    factoryId?: number;
    isActive?: boolean;
    createdAt?: Date;
    updatedAt?: Date;
}