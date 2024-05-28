export const getErrorMessage = (statusCode: number) => {
    if (statusCode === 400) {
        return "Incorrect email or password.";
    }
    else if (statusCode === 401 || statusCode === 403) {
        return "Unauthorized.";
    }
    else if (statusCode >= 500) {
        return "Something went wrong.";
    }

    return "";
}