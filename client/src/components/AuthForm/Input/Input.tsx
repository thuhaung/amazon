import "./Input.scss";

type InputProps = {
    label: string,
    placeholder?: string
    type: "text" | "password",
    autoFocus?: boolean,
    inputHandler: (e: React.ChangeEvent<HTMLInputElement>) => void,
    errorMessage?: string
}

type InputErrorProps = {
    message: string
}

const InputError = ({message}: InputErrorProps) => {
    return (
        <div className="form-input-error">
            {message}
        </div>
    );
}

export const Input = ({label, placeholder, type, autoFocus, inputHandler, errorMessage}: InputProps) => {
    return (
        <div className="form-group">
            <div className="form-label">
                {label}
            </div>
            <input
                type={type}
                className={"form-input " + (errorMessage && " error")}
                autoFocus={autoFocus}
                onChange={inputHandler}
                placeholder={placeholder} />
            {
                errorMessage && 
                <InputError message={errorMessage} />
            }
        </div>
    );
}