export interface Merchant {
  merchantId: string;
  merchantName: string;
  businessName: string;
  isActive: boolean;
  email: string;
  phone: string;
  businessType: string;
  registrationNumber: string;
  createdAt: string;
  updatedAt: string;
}

export interface PaginationInfo {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

export interface MerchantResponse {
  status: number;
  success: boolean;
  message: string;
  data: {
    merchants: Merchant[];
    pagination: PaginationInfo;
  };
}

export interface MerchantFilterState {
  page: number;
  size: number;
  isActive?: boolean;
  merchantName?: string;
  merchantId?: string;
  sortBy?: string;
  sortDirection?: string;
}

export const ACTIVE_FILTERS: MerchantFilterState = {
  page: 0,
  size: 10,
  isActive: true,
};
export const INACTIVE_FILTERS: MerchantFilterState = {
  page: 0,
  size: 10,
  isActive: false,
};
