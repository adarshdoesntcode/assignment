import { useState, useEffect } from "react";
import { getMerchantDetails } from "../services/merchantService";
import { MerchantDetail } from "../types/merchant";

interface UseMerchantDetailsResult {
    data: MerchantDetail | null;
    loading: boolean;
    error: Error | null;
    refetch: () => void;
}

export const useMerchantDetails = (
    merchantId: string
): UseMerchantDetailsResult => {
    const [data, setData] = useState<MerchantDetail | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    const fetchMerchantDetails = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await getMerchantDetails(merchantId);
            setData(response.data);
        } catch (err) {
            setError(err as Error);
            console.error("Error fetching merchant details:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMerchantDetails();
    }, [merchantId]);

    return {
        data,
        loading,
        error,
        refetch: fetchMerchantDetails,
    };
};
