import { useState } from "react";
import { updateMerchant } from "../services/merchantService";
import { MerchantUpdatePayload, MerchantDetail } from "../types/merchant";
import { toast } from "sonner";

interface UseMerchantMutationResult {
    mutate: (merchantId: string, payload: MerchantUpdatePayload) => Promise<void>;
    loading: boolean;
    error: Error | null;
    success: boolean;
    data: MerchantDetail | null;
}

export const useMerchantMutation = (): UseMerchantMutationResult => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);
    const [success, setSuccess] = useState(false);
    const [data, setData] = useState<MerchantDetail | null>(null);

    const mutate = async (merchantId: string, payload: MerchantUpdatePayload) => {
        try {
            setLoading(true);
            setError(null);
            setSuccess(false);

            const response = await updateMerchant(merchantId, payload);

            setData(response.data);
            setSuccess(true);
            toast.success(response.message || "Merchant updated successfully");
        } catch (err) {
            const errorObj = err as Error;
            setError(errorObj);
            setSuccess(false);
            // Error toast is already handled by the API interceptor
        } finally {
            setLoading(false);
        }
    };

    return {
        mutate,
        loading,
        error,
        success,
        data,
    };
};
