import { useEffect } from "react";
import { ReactChildren } from "../types/types";
import { useNavigate, useLocation } from "react-router-dom";
import { Spinner } from "../components/Misc/Spinner/Spinner";
import { useAuth } from "../hooks/useAuth";
import { useAxios } from "../hooks/useAxios";

export const ProtectedRoute = ({ children } : ReactChildren) => {
    const { isAuthenticated, isLoading } = useAuth();
    const { isRefreshing } = useAxios();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if (!isAuthenticated && !isLoading && !isRefreshing) { 
            navigate("/auth/sign-in", {
                state: {
                    from: location
                }
            });
        }
    }, [isAuthenticated, isLoading]);

    if (isLoading) {
        return (
            <div style={{
                width: "100%", 
                height: "100vh", 
                display: "flex", 
                justifyContent: "center", 
                alignItems: "center"
            }}>
                <Spinner style={{width: "40px", height: "40px"}} />
            </div>
        );
    }
    
    return isAuthenticated && children;
}