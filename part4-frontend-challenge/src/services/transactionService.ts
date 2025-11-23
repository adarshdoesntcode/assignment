import { get } from "./api";
import { TransactionResponse } from "../types/transaction";
import { TransactionReportsResponse } from "../types/reports";
import { FilterState } from "../types/common";

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

  try {
    const response = await get<TransactionResponse>(
      `${TRANSACTION_BASE}/${merchantId}`,
      { params }
    );
    return response;
  } catch (error) {
    console.error("Error fetching transactions:", error);
    throw error;
  }
};

export const getTransactionReports = async (): Promise<TransactionReportsResponse> => {
  try {
    const response = await get<TransactionReportsResponse>(`${TRANSACTION_BASE}/reports`);
    return response;
  } catch (error) {
    console.error("Error fetching transaction reports:", error);
    throw error;
  }
};

export default {
  getTransactions,
  getTransactionReports,
};
