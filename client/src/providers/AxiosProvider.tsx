import Axios from "axios";
import { useState } from "react";
import { AxiosContext } from "../context/AxiosContext"
import { ReactChildren } from "../types/types";
import { useLocation, useNavigate } from "react-router-dom";


export const AxiosProvider = ({ children }: ReactChildren) => {
    const [isRefreshing, setIsRefreshing] = useState(false);
    const location = useLocation();
    const navigate = useNavigate();
    const axios = Axios.create({
        baseURL: process.env.REACT_APP_BACKEND_HOST,
        withCredentials: true,
        timeout: 10000
    });

    axios.interceptors.response.use(
        res => res,
        async (err) => {
            if (err?.response?.data?.message === "Access token expired." && !err.config._retry) {
                setIsRefreshing(true);

                const originalRequest = err.config;
                originalRequest._retry = true;

                await axios.get("/auth/refresh-token").then(res => {
                    return axios(originalRequest);
                }).catch(error => {
                    navigate("/auth/sign-in", {
                        state: {
                            from: location
                        }
                    });
                }).finally(() => {
                    setIsRefreshing(false);
                });

                window.location.pathname = location.pathname;
            }
            // else {
            //     console.log("Access token not found.");
            // }

            return Promise.reject(err);
        }
    )


    return (
        <AxiosContext.Provider value={{axios, isRefreshing}}>
            { children }
        </AxiosContext.Provider>
    )
}