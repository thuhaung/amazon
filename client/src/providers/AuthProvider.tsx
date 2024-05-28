import { useEffect, useState } from "react";

import { User, Credentials, ReactChildren } from "../types/types";
import { AuthContext } from "../context/AuthContext";
import { useAxios } from "../hooks/useAxios";
import { getErrorMessage } from "../util/getErrorMessage";
import { useNavigate } from "react-router-dom";

type UserAuth = {
    user: User | null,
    isAuthenticated: boolean,
    error?: string
}

export const AuthProvider = ({ children }: ReactChildren) => {
    const { axios } = useAxios();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [{ user, isAuthenticated, error }, setUserAuth] = useState<UserAuth>({
        user: null,
        isAuthenticated: false,
        error: ""
    });

    const checkStatus = async () => {
        setIsLoading(true);

        axios.get("/auth/check-status").then(res => {
            console.log("user authenticated")
            setUserAuth(prev => ({
                ...prev,
                user: res.data,
                isAuthenticated: true
            }));
        }).catch(err => {
        }).finally(() => {
            setIsLoading(false);
        });
    }

    const login = async (credentials: Credentials) => {
        setIsLoading(true);
        setUserAuth(prev => ({
            ...prev,
            error: ""
        }));

        axios.post("/auth/sign-in", credentials).then(res => {
            setUserAuth(prev => ({
                ...prev,
                user: res.data,
                isAuthenticated: true
            }));
        }).catch(err => {
            setUserAuth(prev => ({
                ...prev,
                error: getErrorMessage(err?.response?.status)
            }));
        }).finally(() => {
            setIsLoading(false);
        });
    }

    const logout = () => {
        setIsLoading(true);

        axios.get("/auth/sign-out").then(res => {
            setUserAuth(prev => ({
                ...prev,
                user: null,
                isAuthenticated: false
            }));
        }).catch(err => {
            setUserAuth(prev => ({
                ...prev,
                error: getErrorMessage(err?.response?.status)
            }));
        }).finally(() => {
            setIsLoading(false);
            navigate("/auth/sign-in");
        });
    }

    useEffect(() => {
        checkStatus();
    }, []);

    return (
        <AuthContext.Provider value={{
            user,
            isAuthenticated,
            isLoading,
            error,
            login,
            logout
        }}>
            { children }
        </AuthContext.Provider>
    );
}