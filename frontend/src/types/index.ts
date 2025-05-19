export interface User {
  id: number;
  username: string;
  role: string;
}

export interface Vehicle {
  id: number;
  make: string;
  model: string;
  year: number;
  licensePlate: string;
  userId: number;
}

export enum WorkOrderStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface WorkOrder {
  id: number;
  status: WorkOrderStatus;
  createdAt: string;
  updatedAt: string;
  userId: number;
  vehicleId: number;
  vehicle?: Vehicle;
  materials?: Material[];
  workHours?: WorkHour[];
}

export interface Material {
  id: number;
  name: string;
  quantity: number;
  price: number;
  workOrderId: number;
}

export interface WorkHour {
  id: number;
  hours: number;
  rate: number;
  description: string;
  workOrderId: number;
}

export interface MaterialDTO {
  name: string;
  quantity: number;
  price: number;
}

export interface WorkHourDTO {
  hours: number;
  rate: number;
  description: string;
}