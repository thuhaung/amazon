import "./SignIn.scss";
import { useState, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import errorIcon from "../../../assets/error.png";
import logo from "../../../assets/amazon-logo.png";
import { AuthForm } from "../../../components/AuthForm/AuthForm";
import { Input } from "../../../components/AuthForm/Input/Input";
import { Layout } from "../../../components/Layout/Layout";
import { useAuth } from "../../../hooks/useAuth";
import { Credentials } from "../../../types/types";


export const SignIn = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { login, isAuthenticated, error } = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [input, setInput] = useState<Credentials>({
        email: "",
        password: ""
    });
    const [errorMessage, setErrorMessage] = useState("");

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const {email, password} = input;

        if (!email || !password) {
            setErrorMessage("Please enter your credentials");
        }
        else if (!emailRegex.test(email)) {
            setErrorMessage("Invalid email");
        }
        else {
            setIsLoading(true);
            setErrorMessage("");

            login(input);
        }
    }


    useEffect(() => {
        const delay = setTimeout(() => {
            setIsLoading(false);
            setErrorMessage(error!);
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
                setErrorMessage("");
            }, 1000);

            if (location.state?.from) {
                navigate(location.state?.from);
            }
            else {
                navigate("/");
            }
        }

        return () => {
            delay && clearTimeout(delay);
        }
    }, [isAuthenticated]);

    return (
        <Layout title="Amazon Sign In" hasNav={false}>
            <div className="sign-in">
                <Link className="logo" to="/">
                    <img
                        src={logo}
                        alt="Amazon" />
                </Link>
                <div className={"error " + (errorMessage && "active")}>
                    <div className="wrapper">
                        <img
                            src={errorIcon}
                            alt="Error" />
                        <div className="message">
                            {errorMessage}
                        </div>
                    </div>
                </div>
                <AuthForm 
                    onSubmit={handleSubmit}
                    isLoading={isLoading}
                    title="Sign in">
                    <Input 
                        label="Email"
                        type="text"
                        inputHandler={(e) => setInput(prev => ({
                            ...prev,
                            email: e.target.value
                        }))}
                        autoFocus={true} />
                    <Input 
                        label="Password"
                        type="password"
                        inputHandler={(e) => setInput(prev => ({
                            ...prev,
                            password: e.target.value
                        }))} />
                </AuthForm>
                <div className="new-to-amazon">
                    <div className="line"></div>
                    <div className="text">
                        New to Amazon?
                    </div>
                    <Link className="register" to="/auth/sign-up">
                        Create your Amazon account
                    </Link>
                </div>
            </div>
        </Layout>
        
    );
}