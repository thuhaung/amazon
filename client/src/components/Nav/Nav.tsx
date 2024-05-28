import "./Nav.scss";
import { useEffect, useState } from "react";
import notificationEmpty from "../../assets/notification-empty.png";
import logo from "../../assets/amazon-logo-white.png";
import search from "../../assets/search.png";
import cart from "../../assets/cart.png";
import arrowDown from "../../assets/arrow-down.png";
import locationIcon from "../../assets/location.png";
import account from "../../assets/account.png";
import { Link } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { getFirstName } from "../../util/getFirstName";
import { isSetupFinished } from "../../util/isSetupFinished";
import { useAxios } from "../../hooks/useAxios";
import { Location } from "../../types/types";

export const Nav = () => {
    const { axios } = useAxios();
    const { user, isAuthenticated, logout } = useAuth();
    const [toggleMenu, setToggleMenu] = useState(false);
    const [rotateDeg, setRotateDeg] = useState(0);
    const [addresses, setAddresses] = useState<Location[]>([]);
    const [location, setLocation] = useState<Location>();
    const [toggleLocation, setToggleLocation] = useState(false);

    const menuItems = [
        {
            id: "profile-category",
            name: "Your profile",
            canSeeAll: true,
            overview: "/dashboard/profile",
            subItems: [
                {
                    id: "personal-info",
                    name: "Personal information"
                },
                {
                    id: "addresses",
                    name: "Addresses"
                },
                {
                    id: "security",
                    name: "Security"
                }
            ]
        },
        {
            id: "account-category",
            name: "Your account",
            canSeeAll: true,
            overview: "/",
            subItems: [
                {
                    id: "lists",
                    name: "Lists"
                },
                {
                    id: "messages",
                    name: "Messages"
                }
            ]
        },
        {
            id: "orders",
            name: "Your orders",
            canSeeAll: true,
            overview: "/",
            subItems: [
                {
                    id: "processing",
                    name: "Processing"
                },
                {
                    id: "shipping",
                    name: "Shipping"
                },
                {
                    id: "completed",
                    name: "Completed"
                },
                {
                    id: "cancelled",
                    name: "Cancelled"
                },
                {
                    id: "returns",
                    name: "Returns"
                }
            ]
        },
        {
            id: "sign-out",
            canSeeAll: false,
            name: "Sign out"
        }
    ];

    const onToggleMenu = () => {
        if (!toggleMenu) {
            setRotateDeg(-180);
        }
        else {
            
            setRotateDeg(0);
        }

        setToggleMenu(prev => !prev);
    }

    const sortAddresses = (arr: Location[]) => {
        let index = arr.findIndex((item: any) => item.default);

        if (index > 0) {
            let temp = arr[index];
            arr[index] = arr[0];
            arr[0] = temp;
        }

        return arr;
    }

    const setDefault = (index: number) => {
        axios.patch(`/user/${user?.id}/address/${addresses[index].id}/default`).then(res => {
            setToggleLocation(false);
            setLocation(addresses[index]);
        }).catch(err => {
            console.log(err);
        });
    }

    useEffect(() => {
        if (user) {
            axios.get(`/user/${user.id}/address`).then(res => {
                const response = res.data;
                if (response) {
                    setAddresses([...sortAddresses(response)]);

                    const index: number = response.findIndex((item: any) => item.default);
                
                    if (index >= 0) {
                        setLocation(response[index]);
                    }
                }
            }).catch(err => {
                console.log(err);
            });
        }
    }, [user, toggleLocation]);

    useEffect(() => {
        if (!toggleMenu || !toggleLocation) {
            document.body.style.overflow = "auto";
        }
        else {
            document.body.style.overflow = "hidden";
        }
    }, [toggleMenu, toggleLocation]);

    return (
        <div className="nav">
            <div className="main-wrapper">
                <div className={"grey-background " + ((toggleMenu || toggleLocation) && "active")}></div>
                <div className={"menu-mobile " + (toggleMenu && "active")}>
                    <div className="welcome">
                        <div className="welcome-text">
                            Hello{isAuthenticated ? (" " + getFirstName(user!.fullName)) : ""},
                        </div>
                        <div className="close" onClick={() => setToggleMenu(false)}>
                            x
                        </div>
                    </div>
                    <div className="categories">
                        {
                            menuItems.map(category =>
                                (
                                    <div key={category.id} className="category">
                                        <div className="category-info">
                                            <div 
                                                className="name" 
                                                onClick={category.id === "sign-out"? logout : undefined}>
                                                {category.name}
                                            </div>
                                            {
                                                category.canSeeAll &&
                                                <Link 
                                                    className="see-all" 
                                                    to={category.overview!}>
                                                    See all
                                                </Link>
                                            }
                                        </div>
                                        {
                                            category.subItems &&
                                            <div className="sub-items">
                                                {category.subItems?.map(item =>
                                                    <Link 
                                                        className="item" 
                                                        key={item.id}
                                                        to={`${category.overview}/${item.id}`}>
                                                        {item.name}
                                                    </Link>
                                                )}
                                            </div>
                                        }
                                    </div>
                                )
                            )
                        }
                    </div>
                </div>
                <div className="left-hand-side">
                    <div className="intro">
                        <Link className="logo" to="/">
                            <img
                                src={logo}
                                alt="Amazon logo" />
                        </Link>
                        {
                            user ?
                            <div className="location">
                                <div className="label">
                                    Deliver to
                                </div>
                                <div className="specific-region" onClick={() => setToggleLocation(prev => !prev)}>
                                    <img
                                        src={locationIcon}
                                        alt="Location icon" />
                                    <div className="region">
                                        {location?.country || "Worldwide"}
                                    </div>
                                </div>
                                <div className={"location-dropdown " + (toggleLocation ? "active" : "")}>
                                    {
                                        addresses.length === 0 ?
                                        <div className="add-address">
                                            Don't have a shipping address? {" "}
                                            <Link to="/dashboard/profile/addresses">
                                                Add one here.
                                            </Link>
                                        </div> 
                                        :
                                        <div className="set-default">
                                            Set default shipping address
                                        </div>
                                    }
                                    {
                                        addresses.length !== 0 &&
                                        addresses.map(
                                            (address, index) =>
                                            <div className="location-option" onClick={() => setDefault(index)}>
                                                <div className="is-default">
                                                    {address.default ? "Default" : ""}
                                                </div>
                                                <div className="field">
                                                    {address.building}
                                                </div>
                                                <div className="field">
                                                    {address.street}
                                                </div>
                                                <div className="field">
                                                    {address.city}
                                                </div>
                                                <div className="field">
                                                    {address.state}
                                                </div>
                                                <div className="field">
                                                    {address.country}
                                                </div>
                                            </div>
                                        )
                                    }
                                </div>
                            </div>
                            : ""
                        }
                    </div>
                    <div className="right-hand-side-mobile">
                        <div className="item account" onClick={() => setToggleMenu(true)}>
                            <img
                                src={account}
                                alt="Your account" />
                        </div>
                        <div className="item notifications">
                            <img
                                src={notificationEmpty}
                                alt="Your notifications" />
                        </div>
                        <div className="item cart">
                            <img
                                src={cart}
                                alt="Shopping cart" />
                        </div>
                    </div>
                    <div className="search-bar">
                        <input
                            type="text"
                            className="search-input"
                            placeholder="Search Amazon" />
                        <div className="search-button">
                            <img
                                src={search}
                                alt="Search icon" />
                        </div>
                    </div>
                </div>
                <div className="right-hand-side">
                    <div className="account" onClick={onToggleMenu}>
                        <div className="welcome">
                            Hello{isAuthenticated ? (" " + getFirstName(user!.fullName)) : ""},
                        </div>
                        <div className="dropdown">
                            <div className="text">
                                Account
                            </div>
                            <img
                                src={arrowDown}
                                style={{"transform": `rotate(${rotateDeg}deg)`}}
                                alt="Arrow drop down" />
                            <div 
                                className={
                                    "menu " + 
                                    (toggleMenu ? " active" : "") + 
                                    (isAuthenticated ? " authenticated" : "")
                                }>
                                {
                                    isAuthenticated ?
                                    <div className="menu-wrapper">
                                        <Link className="dashboard" to="/dashboard">
                                            Menu dashboard {" "}
                                            <div className="arrow">→</div>
                                        </Link>
                                        <div className="categories">
                                            <div className="left">
                                                {
                                                    menuItems.map((category, index) => {
                                                        if (index < 2) {
                                                            return (
                                                                <div className="category" key={category.id}>
                                                                    <div className="category-info">
                                                                        <div className="name">
                                                                            {category.name}
                                                                        </div>
                                                                        {
                                                                            category.canSeeAll &&
                                                                            <Link 
                                                                                className="see-all" 
                                                                                to={category.overview!}>
                                                                                See all
                                                                            </Link>
                                                                        }
                                                                    </div>
                                                                    {
                                                                        category.subItems &&
                                                                        <div className="sub-items">
                                                                            {category.subItems?.map(item =>
                                                                                <Link 
                                                                                    className="item" 
                                                                                    key={item.id} 
                                                                                    to={`${category.overview}/${item.id}`}>
                                                                                    {item.name}
                                                                                </Link>
                                                                            )}
                                                                        </div>
                                                                    }
                                                                </div>
                                                            )
                                                        }
                                                    })
                                                }
                                            </div>
                                            <div className="right">
                                                {
                                                    menuItems.map((category, index) => {
                                                        if (index >= 2) {
                                                            return (
                                                                <div className="category" key={category.id}>
                                                                    <div className="category-info">
                                                                        <div 
                                                                            className="name" 
                                                                            onClick={category.id === "sign-out"? logout : undefined}>
                                                                            {category.name}
                                                                        </div>
                                                                        {
                                                                            category.canSeeAll &&
                                                                            <Link 
                                                                                className="see-all" 
                                                                                to={category.overview!}>
                                                                                See all
                                                                            </Link>
                                                                        }
                                                                    </div>
                                                                    {
                                                                        category.subItems &&
                                                                        <div className="sub-items">
                                                                            {category.subItems?.map(item =>
                                                                                <Link 
                                                                                    className="item" 
                                                                                    key={item.id}
                                                                                    to={`${category.overview}/${item.id}`}>
                                                                                    {item.name}
                                                                                </Link>
                                                                            )}
                                                                        </div>
                                                                    }
                                                                </div>
                                                            )
                                                        }
                                                    })
                                                }
                                            </div>
                                        </div>
                                    </div>
                                    :
                                    <div className="auth-prompt">
                                        <Link className="sign-in" to="/auth/sign-in">
                                            Sign in
                                        </Link>
                                        <div className="register">
                                            New customer? <Link to="/auth/sign-up">Start here</Link>.
                                        </div>
                                    </div>
                                }
                            </div>
                        </div>
                    </div>
                    <div className="notifications">
                        <img
                            src={notificationEmpty}
                            alt="Notification icon" />
                        <div className="label">
                            Notifications
                        </div>
                    </div>
                    <div className="cart">
                        <img
                            src={cart}
                            alt="Cart"/>
                        <div className="label">
                            Cart
                        </div>
                    </div>
                </div>
                <div className="location-mobile">
                    <img
                        src={locationIcon}
                        alt="Location picker" />
                    <div className="deliver">
                        Deliver to <div className="region">Vietnam</div>
                    </div>
                </div>
            </div>
            {
                user !== null && (!isSetupFinished(user) || !user.isVendor) ?
                <div className="setup-notification">
                    {
                        !isSetupFinished(user) && 
                        <Link className="text" to="/dashboard/profile/security">
                            Finish setting up your account {" "}
                        </Link>
                    }
                    {
                        !user.isVendor &&
                        <Link className="text" to="/dashboard/profile/security">
                            Become a vendor
                        </Link>
                    }
                </div>
                : ""
            }
            {/* {
                user !== null && !isSetupFinished(user) &&
                <div className="setup-notification">
                    <Link className="text" to="/dashboard/profile/security">
                        Finish setting up your account {" "}
                    </Link>
                    <img
                        src={arrowRight} />
                </div>
            } */}
        </div>
    );
}