import { User } from "../types/types";

export const isSetupFinished = (user: User) => {
    return (user.gender) && (user.mobile) && (user.isVerified);
}