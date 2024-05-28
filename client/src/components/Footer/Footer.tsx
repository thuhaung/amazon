import { Link } from "react-router-dom";
import "./Footer.scss";
import logo from "../../assets/amazon-logo-white.png";
import { useAuth } from "../../hooks/useAuth";

export const Footer = () => {
    const { isAuthenticated } = useAuth();
    const scrollToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }

    return (
        <div className="footer">
            {
                !isAuthenticated &&
                <div className="auth-prompt">
                    <div className="box">
                        <Link className="sign-in" to="/auth/sign-in">
                            Sign in
                        </Link>
                        <div className="register">
                            New customer? <Link to="/auth/sign-up">Start here</Link>.
                        </div>
                    </div>
                </div>
            }
            <div className="back-to-top" onClick={scrollToTop}>
                Back to top
            </div>
            <div className="ending">
                <div className="wrapper">
                    <Link className="logo" to="/">
                        <img
                            src={logo}
                            alt="Amazon" />
                    </Link>
                    <div className="settings">
                        <div className="setting language">
                            English
                        </div>
                        <div className="setting currency">
                            USD - U.S Dollar
                        </div>
                        <div className="setting region">
                            United States
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}