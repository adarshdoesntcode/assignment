import { useState, useEffect } from "react";
import { getMerchants } from "../services/merchantService";
import {
    Merchant,
    MerchantFilterState,
    PaginationInfo,
} from "../types/merchant";

interface UseMerchantsResult {
    data: {
        merchants: Merchant[];
        pagination: PaginationInfo;
    } | null;
    loading: boolean;
    error: Error | null;
    refetch: () => void;
}

export const useMerchants = (
    filters: MerchantFilterState
): UseMerchantsResult => {
    const [data, setData] = useState<UseMerchantsResult["data"]>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fetchMerchants = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await getMerchants(filters);
            setData(response.data);
        } catch (err) {
            setError(err as Error);
            console.error("Error fetching merchants:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMerchants();
    }, [
        filters.page,
        filters.size,
        filters.merchantName,
        filters.merchantId,
        filters.sortBy,
        filters.sortDirection,
    ]);

    return {
        data,
        loading,
        error,
        refetch: fetchMerchants,
    };
};
