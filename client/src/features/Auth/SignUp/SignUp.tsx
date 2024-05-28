import "./SignUp.scss";
import { useState, useEffect } from "react";
import errorIcon from "../../../assets/error.png";
import { Link, useNavigate } from "react-router-dom";
import logo from "../../../assets/amazon-logo.png";
import { AuthForm } from "../../../components/AuthForm/AuthForm";
import { Input } from "../../../components/AuthForm/Input/Input";
import { Layout } from "../../../components/Layout/Layout";
import { useAxios } from "../../../hooks/useAxios";
import { useAuth } from "../../../hooks/useAuth";

export const SignUp = () => {
    const { axios } = useAxios();
    const { isAuthenticated, login, error } = useAuth();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [input, setInput] = useState<Record<string, string>>({
        fullName: "",
        email: "",
        password: ""
    });
    const [requestError, setRequestError] = useState("");
    const [inputError, setInputError] = useState(input);

    const getFieldName = (key: string) => {
        return key.split(/(?=[A-Z])/).join(" ").toLowerCase();
    }

    const errorHandler = (key: string) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const nameRegex = /^([a-zA-Z]{2,}\s[a-zA-Z]{1,}'?-?[a-zA-Z]{2,}\s?([a-zA-Z]{1,})?)/;
        let message = "";

        if (!input[key]) {
            message = "Enter your " + getFieldName(key);
        }
        else {
            switch(key) {
                case "fullName":
                    if (!nameRegex.test(input[key])) {
                        message = "Invalid name"; 
                    }
                    else if (input[key].length >= 256) {
                        message = "Name length exceeds limit";
                    }
                    break;
                case "email":
                    if (!emailRegex.test(input[key])) {
                        message = "Invalid email";
                    }
                    else if (input[key].length >= 256) {
                        message = "Email length exceeds limit";
                    }
                    break;
                case "password":
                    if (input[key].length < 6) {
                        message = "Must have at least 6 characters";
                    }
                    break;
            }
        }

        return message;
    }
    
    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        let errorObj = inputError;

        Object.keys(input).forEach(key => {
            errorObj = {
                ...errorObj,
                [key]: errorHandler(key)
            }
        });

        setInputError(errorObj);

        if (Object.values(errorObj).every(x => x === "")) {
            console.log("sending");
            setIsLoading(true);
            
            axios.post("/auth/sign-up", input).then(res => {
                setRequestError("");
                
                login({email: input.email, password: input.password});
            }).catch(err => {
                setIsLoading(false);
                setRequestError(err.response?.data?.message);
            });
        }
    }

    useEffect(() => {
        const delay = setTimeout(() => {
            setIsLoading(false);
            setRequestError(error!);
        }, 1000);

        return () => {
            clearTimeout(delay);
        }
    }, [error]);

    useEffect(() => {
        let delay: NodeJS.Timeout;

        if (isAuthenticated) {
            delay = setTimeout(() => {
                setIsLoading(false);
                setRequestError("");
            }, 1000);

            navigate("/");
        }

        return () => {
            delay && clearTimeout(delay);
        }
    }, [isAuthenticated]);

    return (
        <Layout title="Amazon Registration" hasNav={false}>
            <div className="sign-up">
                <Link className="logo" to="/">
                    <img
                        src={logo}
                        alt="Amazon" />
                </Link>
                <div className={"request-error " + (requestError && "active")}>
                    <div className="wrapper">
                        <img
                            src={errorIcon}
                            alt="Error" />
                        <div className="message">
                            {requestError}
                        </div>
                    </div>
                </div>
                <AuthForm
                    onSubmit={handleSubmit}
                    isLoading={isLoading}
                    title="Create an account">
                    <Input 
                        label="Full name"
                        type="text"
                        inputHandler={(e) => setInput(prev => ({
                            ...prev,
                            fullName: e.target.value
                        }))}
                        autoFocus={true}
                        errorMessage={inputError.fullName} />
                    <Input 
                        label="Email"
                        type="text"
                        inputHandler={(e) => setInput(prev => ({
                            ...prev,
                            email: e.target.value
                        }))}
                        errorMessage={inputError.email} />
                    <Input 
                        label="Password"
                        type="password"
                        inputHandler={(e) => setInput(prev => ({
                            ...prev,
                            password: e.target.value
                        }))}
                        errorMessage={inputError.password} />
                </AuthForm>
                <div className="already-have-account">
                    <div className="line"></div>
                    <div className="text">
                        Already have an account?
                    </div>
                    <Link className="sign-in" to="/auth/sign-in">
                        Sign in
                    </Link>
                </div>
            </div>
        </Layout>
    );
}