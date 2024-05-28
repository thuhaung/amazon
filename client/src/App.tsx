import "./App.scss";
import { BrowserRouter } from "react-router-dom";
import { AxiosProvider } from "./providers/AxiosProvider";
import { AuthProvider } from "./providers/AuthProvider";
import { CookiesProvider } from "react-cookie";
import { AppRouter } from "./routes/AppRouter";

const App = () => {
    return (
        <BrowserRouter>
            <AxiosProvider>
                <AuthProvider>
                    <CookiesProvider defaultSetOptions={{path: "/"}}>
                        <AppRouter />
                    </CookiesProvider>
                </AuthProvider>
            </AxiosProvider>
        </BrowserRouter>
        // <RouterProvider router={Routes()} />
    ); 
}

export default App;