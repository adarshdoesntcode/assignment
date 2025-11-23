import axios, { AxiosInstance, AxiosRequestConfig, AxiosError } from "axios";
import { toast } from "sonner";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api/v1";
const API_TIMEOUT = parseInt(import.meta.env.VITE_API_TIMEOUT || "30000");

/**
 * Axios instance with default configuration
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Request interceptor - Add auth tokens, logging, etc.
 */
apiClient.interceptors.request.use(
  (config) => {
    // TODO: Add authentication token if needed
    // const token = localStorage.getItem('authToken');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }

    console.log(
      `[API Request] ${config.method?.toUpperCase()} ${config.url}`,
      config.params
    );
    return config;
  },
  (error) => {
    console.error("[API Request Error]", error);
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - Handle errors globally
 */
apiClient.interceptors.response.use(
  (response) => {
    console.log(`[API Response] ${response.config.url}`, response.status);
    return response;
  },
  (error: AxiosError) => {
    console.error(
      "[API Response Error]",
      error.response?.status,
      error.message
    );

    const status = error.response?.status;
    const errorMessage =
      (error.response?.data as any)?.message ||
      error.message ||
      "An unexpected error occurred";

    // Handle specific error cases with Toasts
    if (status === 400) {
      toast.error(`Bad Request: ${errorMessage}`);
    } else if (status === 401) {
      toast.error(errorMessage);
    } else if (status === 403) {
      toast.error(errorMessage);
    } else if (status === 404) {
      toast.error(errorMessage);
    } else {
      toast.error(errorMessage);
    }

    return Promise.reject(error);
  }
);

/**
 * Generic GET request
 */
export const get = <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  return apiClient.get<T>(url, config).then((response) => response.data);
};

/**
 * Generic POST request
 */
export const post = <T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig
): Promise<T> => {
  return apiClient.post<T>(url, data, config).then((response) => response.data);
};

/**
 * Generic PUT request
 */
export const put = <T>(
  url: string,
  data?: unknown,
  config?: AxiosRequestConfig
): Promise<T> => {
  return apiClient.put<T>(url, data, config).then((response) => response.data);
};

/**
 * Generic DELETE request
 */
export const del = <T>(
  url: string,
  config?: AxiosRequestConfig
): Promise<T> => {
  return apiClient.delete<T>(url, config).then((response) => response.data);
};

export default apiClient;
