import { useAuth } from "../../hooks/useAuth";
import { isSetupFinished } from "../../util/isSetupFinished";
import { Footer } from "../Footer/Footer";
import { Head } from "../Head/Head";
import { Nav } from "../Nav/Nav";
import "./Layout.scss";

type LayoutProps = {
    children: React.ReactNode,
    title: string,
    hasNav?: boolean
}

export const Layout = ({children, title, hasNav = true} : LayoutProps) => {
    const { user } = useAuth();

    return (
        <>
            <Head title={title} />
            {hasNav && <Nav />}
            <div className={"children " + (user !== null && !isSetupFinished(user) ? "unfinished" : "")}>
                {children}
            </div>
            {hasNav && <Footer />}
        </>
    );
}