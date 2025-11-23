import { FilterState } from "./common";

export interface Transaction {
  txnId: number;
  amount: number;
  currency: string;
  status: "completed" | "pending" | "failed" | "reversed";
  timestamp: string;
  cardType: string;
  cardLast4: string;
  acquirer: string;
  issuer: string;
  details: TransactionDetail[];
}

export interface TransactionDetail {
  detailId: number;
  type: "fee" | "tax" | "adjustment" | "refund";
  amount: number;
  description: string;
}

export interface TransactionSummary {
  totalTransactions: number;
  totalAmount: number;
  currency: string;
  byStatus: {
    completed: number;
    pending: number;
    failed: number;
  };
}

export interface TransactionResponse {
  data: {
    merchantId: string;
    dateRange: {
      start: string;
      end: string;
    };
    summary: TransactionSummary;
    transactions: Transaction[];
    pagination: PaginationInfo;
  };
}

export interface PaginationInfo {
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

export const DEFAULT_FILTERS: FilterState = {
  page: 0,
  size: 10,
  startDate: undefined,
  endDate: undefined,
  status: undefined,
};
