export interface DashboardDTO {
  totalDevices?: number;
  totalWorkingDevices?: number;
  totalDevicesInMaintenance?: number;
  totalDevicesMaintained?: number;
  totalMaintained?: number;
  maintenanceExecutionCount?: number;
  acceptedPeriodicMaintenanceDeviceCount?: number;
  dueMaintenanceDeviceCount?: number;
  faultyDeviceCount?: number;
  criticalIncidentReportCount?: number;
  year?: number;
  month?: number;
  appliedBranchIds?: number[];
  queryTimingsMs?: Record<string, number>;
  totalQueryTimeMs?: number;
}

export interface DashboardFilter {
  branchIds?: number[];
  branchNames?: string[];
  branchId?: number;
  branchName?: string;
  teamId?: number;
  groupId?: number;
  year?: number;
  month?: number;
  deviceName?: string;
  maintenanceDueType?: 'OVERDUE' | 'UPCOMING' | 'ALL' | string;
  includeTiming?: boolean;
}

export type DashboardDetailType =
  | 'WORKING_DEVICES'
  | 'WORKING_PERCENT'
  | 'MONTHLY_PLAN'
  | 'YEARLY_PLAN'
  | 'MAINTAINED_DEVICES'
  | 'MAINTENANCE_EXECUTIONS'
  | 'ACCEPTED_MAINTENANCE'
  | 'DUE_MAINTENANCE'
  | 'FAULTY_DEVICES'
  | 'CRITICAL_INCIDENTS';

export interface DashboardMaintenanceExecutionDTO {
  planResultId?: number;
  planDetailId?: number;
  deviceId?: number;
  deviceCode?: string;
  deviceName?: string;
  planName?: string;
  dateTest?: string | Date;
  resultStatus?: number;
  branchName?: string;
  teamName?: string;
  lineName?: string;
}

export interface DashboardPlanDetailRowDTO {
  planDetailId?: number;
  planDetailStatus?: number;
  estimatedTime?: string | Date;
  planId?: number;
  planName?: string;
  deviceId?: number;
  deviceCode?: string;
  deviceName?: string;
  branchName?: string;
  teamName?: string;
  lineName?: string;
}

export interface DeviceMaintenanceDTO {
  deviceId?: number;
  deviceCode?: string;
  deviceName?: string;
  branchName?: string;
  teamName?: string;
  lineName?: string;
  deviceGroupName?: string;
  dateTest?: string | Date;
  nextTest?: string | Date;
  maintenanceTime?: number;
  daysDiff?: number;
  status?: string;
}
