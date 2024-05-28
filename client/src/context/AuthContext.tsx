import { createContext } from "react";
import { User, Credentials } from "../types/types";


export const AuthContext = createContext<{
    user: User | null,
    isAuthenticated: boolean,
    error?: string,
    isLoading: boolean,
    login: (credentials: Credentials) => Promise<void>,
    logout: () => void
}>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
    login: async () => {},
    logout: () => {}
});