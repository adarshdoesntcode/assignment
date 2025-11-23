import { useState, useEffect } from "react";
import { getTransactionReports } from "../services/transactionService";
import { TransactionReportsData } from "../types/reports";

interface UseTransactionReportsResult {
    data: TransactionReportsData | null;
    loading: boolean;
    error: Error | null;
    refetch: () => void;
}

export const useTransactionReports = (): UseTransactionReportsResult => {
    const [data, setData] = useState<TransactionReportsData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fetchReports = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await getTransactionReports();
            setData(response.data);
        } catch (err) {
            setError(err as Error);
            console.error("Error fetching transaction reports:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchReports();
    }, []);

    return {
        data,
        loading,
        error,
        refetch: fetchReports,
    };
};
