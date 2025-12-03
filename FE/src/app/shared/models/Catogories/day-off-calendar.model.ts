export class DayOffCalendar {
    id?: number;
    code?: string;
    name?: string;
    date?: Date;
    dayOfWeek?: number; // 0 - Chủ nhật, 1 - Thứ hai, ..., 6 - Thứ bảy
    type?: string;
    isDayOff?: number;
    description?: string;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;

    branch?: any;
    team?: any;
}