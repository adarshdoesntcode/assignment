import { get, put, post } from "./api";
import {
  MerchantResponse,
  MerchantFilterState,
  MerchantDetailResponse,
  MerchantUpdatePayload,
  MerchantUpdateResponse,
  MerchantCreatePayload,
  MerchantCreateResponse
} from "../types/merchant";

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

export const getMerchantDetails = async (
  merchantId: string
): Promise<MerchantDetailResponse> => {
  try {
    const response = await get<MerchantDetailResponse>(`${MERCHANT_BASE}/${merchantId}`);
    return response;
  } catch (error) {
    console.error("Error fetching merchant details:", error);
    throw error;
  }
};

export const updateMerchant = async (
  merchantId: string,
  payload: MerchantUpdatePayload
): Promise<MerchantUpdateResponse> => {
  try {
    const response = await put<MerchantUpdateResponse>(`${MERCHANT_BASE}/${merchantId}`, payload);
    return response;
  } catch (error) {
    console.error("Error updating merchant:", error);
    throw error;
  }
};

export const createMerchant = async (
  payload: MerchantCreatePayload
): Promise<MerchantCreateResponse> => {
  try {
    const response = await post<MerchantCreateResponse>(MERCHANT_BASE, payload);
    return response;
  } catch (error) {
    console.error("Error creating merchant:", error);
    throw error;
  }
};
