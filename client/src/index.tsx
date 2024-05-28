import React from 'react';
import App from './App';
import { createRoot } from "react-dom/client";
import { AxiosProvider } from './providers/AxiosProvider';
import { AuthProvider } from './providers/AuthProvider';
import { CookiesProvider } from 'react-cookie';

const root = createRoot(document.getElementById("root")!);
root.render(
    <React.StrictMode>
        <App />
        {/* <AxiosProvider>
            <AuthProvider>
                <CookiesProvider defaultSetOptions={{path: "/"}}>
                    <App />
                </CookiesProvider>
            </AuthProvider>
        </AxiosProvider> */}
    </React.StrictMode>
);

