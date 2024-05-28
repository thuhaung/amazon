import { Routes, Route } from "react-router-dom";
import { SignIn } from "../features/Auth/SignIn/SignIn";
import { SignUp } from "../features/Auth/SignUp/SignUp";
import { Homepage } from "../features/Homepage/Homepage";
import { ProtectedRoute } from "./ProtectedRoute";
import { Dashboard } from "../features/Dashboard/Dashboard";
import { PersonalInfo } from "../features/Profile/PersonalInfo/PersonalInfo";
import { ProfileOverview } from "../features/Profile/ProfileOverview";
import { Addresses } from "../features/Profile/Addresses/Addresses";
import { Security } from "../features/Profile/Security/Security";

export const AppRouter = () => {
    return (
        <Routes>
            <Route path="/" element={<Homepage />} />
            <Route path="/auth/">
                <Route path="sign-in" element={<SignIn />} />
                <Route path="sign-up" element={<SignUp />} />
            </Route>
            <Route path="/dashboard" element={<ProtectedRoute children={<Dashboard />} />} />
            <Route path="/dashboard/profile" element={<ProtectedRoute children={<ProfileOverview />} />}>
                <Route path="personal-info" element={<ProtectedRoute children={<PersonalInfo />} />} />
                <Route path="addresses" element={<ProtectedRoute children={<Addresses />} />} />
                <Route path="security" element={<ProtectedRoute children={<Security />} />} />
            </Route>
        </Routes>
    );
}