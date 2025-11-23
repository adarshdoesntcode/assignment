import { get } from "./api";
import { MerchantResponse, MerchantFilterState } from "../types/merchant";

const MERCHANT_BASE = "/merchants";

export const getMerchants = async (
  filters: MerchantFilterState
): Promise<MerchantResponse> => {
  const params: Record<string, any> = {
    page: filters.page,
    size: filters.size,
  };

  if (filters.merchantName) {
    params.merchantName = filters.merchantName;
  }

  if (filters.merchantId) {
    params.merchantId = filters.merchantId;
  }

  if (filters.sortBy) {
    params.sortBy = filters.sortBy;
  }

  if (filters.sortDirection) {
    params.sortDirection = filters.sortDirection;
  }

  if (filters.isActive !== undefined) {
    params.isActive = filters.isActive;
  }

  try {
    const response = await get<MerchantResponse>(MERCHANT_BASE, { params });
    return response;
  } catch (error) {
    console.error("Error fetching merchants:", error);
    throw error;
  }
};
