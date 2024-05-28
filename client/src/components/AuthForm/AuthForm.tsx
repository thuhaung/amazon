import { Spinner } from "../Misc/Spinner/Spinner";
import "./AuthForm.scss";

type AuthFormProps = {
    onSubmit: (e: React.FormEvent<HTMLFormElement>) => void,
    isLoading: boolean,
    children: React.ReactNode,
    title: string,
}

export const AuthForm = ({onSubmit, isLoading, children, title}: AuthFormProps) => {
    return (
        <form className="auth-form" onSubmit={onSubmit}>
            <div className="title">
                {title}
            </div>
            {children}
            <button className="continue" type="submit">
                {
                    isLoading ?
                    <Spinner /> : "Continue"
                }
            </button>
        </form>
    );
}
