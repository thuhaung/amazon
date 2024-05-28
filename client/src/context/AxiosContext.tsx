import { AxiosInstance } from "axios";
import { createContext } from "react";

export const AxiosContext = createContext<{
    isRefreshing: boolean,
    axios: AxiosInstance
}>({ 
    isRefreshing: false,
    axios: {} as AxiosInstance
});