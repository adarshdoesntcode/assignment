import { get } from "./api";
import { TransactionResponse } from "../types/transaction";
import { FilterState } from "@/types/common";

const TRANSACTION_BASE = "/transactions";

export const getTransactions = async (
  merchantId: string,
  filters: FilterState
): Promise<TransactionResponse> => {
  const params: Record<string, any> = {
    page: filters.page,
    size: filters.size,
  };

  if (filters.status) {
    params.status = filters.status;
  }

  if (filters.startDate) {
    params.startDate = filters.startDate;
  }
  if (filters.endDate) {
    params.endDate = filters.endDate;
  }

  const url = `${TRANSACTION_BASE}/${merchantId}`;

  try {
    const response = await get<TransactionResponse>(url, { params });
    return response;
  } catch (error) {
    console.error("Error fetching transactions:", error);
    throw error;
  }
};

export default {
  getTransactions,
};
