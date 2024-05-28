import { Layout } from "../../components/Layout/Layout";
import { Outlet, useNavigate, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import "./ProfileOverview.scss";
import { Country, Location } from "../../types/types";
import { useAxios } from "../../hooks/useAxios";
import { useAuth } from "../../hooks/useAuth";

export interface OutletContext {
    popupActive: boolean,
    setCurrentOutlet: React.Dispatch<React.SetStateAction<string>>,
    setPopupActive: React.Dispatch<React.SetStateAction<boolean>>,
    setLocation: React.Dispatch<React.SetStateAction<Location>>,
    setPopupType: React.Dispatch<React.SetStateAction<string>>
}

export const ProfileOverview = () => {
    const { axios } = useAxios();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [currentOutlet, setCurrentOutlet] = useState<string>(""); 
    const sections = [
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
    ];

    const [location, setLocation] = useState<Location>({
        country: "",
        state: "",
        city: "",
        street: "",
        building: "",
        default: false
    });
    const [popupActive, setPopupActive] = useState(false);
    const [popupType, setPopupType] = useState("Add");
    const [message, setMessage] = useState<{
        message: string,
        status: string
    }>({
        message: "",
        status: ""
    });
    
    const onSelectSection = (index: number) => {
        setCurrentOutlet(sections[index].name);
        navigate(`${sections[index].id}`);
    }

    const onReturnToOverview = () => {
        setCurrentOutlet("");
        navigate("/dashboard/profile");
    }

    const data: Country[] = [
        {
          "id": 1,
          "name": "United States",
          "iso2": "US",
          "capital": "Washington",
          "currency": "USD",
          "currency_symbol": "$",
          "latitude": "38.00000000",
          "longitude": "-97.00000000",
          "states": [
            {
              "id": 1416,
              "name": "California",
              "state_code": "CA",
              "latitude": "36.77826100",
              "longitude": "-119.41793240",
              "type": "state",
              "cities": [
                {
                  "id": 111425,
                  "name": "Arcadia",
                  "latitude": "34.13973000",
                  "longitude": "-118.03534000"
                },
                {
                  "id": 113047,
                  "name": "Burbank",
                  "latitude": "34.18084000",
                  "longitude": "-118.30897000"
                },
                {
                  "id": 114324,
                  "name": "Coachella",
                  "latitude": "33.68030000",
                  "longitude": "-116.17389000"
                },
                {
                  "id": 114529,
                  "name": "Compton",
                  "latitude": "33.89585000",
                  "longitude": "-118.22007000"
                },
                {
                  "id": 115038,
                  "name": "Danville",
                  "latitude": "37.82159000",
                  "longitude": "-121.99996000"
                },
                {
                  "id": 118567,
                  "name": "Hollywood",
                  "latitude": "34.09834000",
                  "longitude": "-118.32674000"
                },
                {
                  "id": 118805,
                  "name": "Huntington Beach",
                  "latitude": "33.66030000",
                  "longitude": "-117.99923000"
                },
                {
                  "id": 119323,
                  "name": "Joshua Tree",
                  "latitude": "34.13473000",
                  "longitude": "-116.31307000"
                },
                {
                  "id": 119708,
                  "name": "Koreatown",
                  "latitude": "34.05779000",
                  "longitude": "-118.30091000"
                },
                {
                  "id": 120738,
                  "name": "Long Beach",
                  "latitude": "33.76696000",
                  "longitude": "-118.18923000"
                },
                {
                  "id": 120784,
                  "name": "Los Angeles",
                  "latitude": "34.05223000",
                  "longitude": "-118.24368000"
                },
                {
                  "id": 121062,
                  "name": "Malibu",
                  "latitude": "34.02577000",
                  "longitude": "-118.78040000"
                },
                {
                  "id": 123781,
                  "name": "Palo Alto",
                  "latitude": "37.44188000",
                  "longitude": "-122.14302000"
                },
                {
                  "id": 125577,
                  "name": "Sacramento",
                  "latitude": "38.58157000",
                  "longitude": "-121.49440000"
                },
                {
                  "id": 125802,
                  "name": "San Diego",
                  "latitude": "32.71571000",
                  "longitude": "-117.16472000"
                },
                {
                  "id": 125809,
                  "name": "San Francisco",
                  "latitude": "37.77493000",
                  "longitude": "-122.41942000"
                },
                {
                  "id": 128899,
                  "name": "West Hollywood",
                  "latitude": "34.09001000",
                  "longitude": "-118.36174000"
                },
              ]
            },
            {
              "id": 1452,
              "name": "New York",
              "state_code": "NY",
              "latitude": "40.71277530",
              "longitude": "-74.00597280",
              "type": "state",
              "cities": [
                {
                  "id": 111108,
                  "name": "Albany",
                  "latitude": "42.65258000",
                  "longitude": "-73.75623000"
                },
                {
                  "id": 112025,
                  "name": "Bedford",
                  "latitude": "41.20426000",
                  "longitude": "-73.64374000"
                },
                {
                  "id": 112757,
                  "name": "Bridgehampton",
                  "latitude": "40.93788000",
                  "longitude": "-72.30092000"
                },
                {
                  "id": 112846,
                  "name": "Bronx",
                  "latitude": "40.82732000",
                  "longitude": "-73.92357000"
                },
                {
                  "id": 112872,
                  "name": "Brooklyn",
                  "latitude": "40.65010000",
                  "longitude": "-73.94958000"
                },
                {
                  "id": 113103,
                  "name": "Bushwick",
                  "latitude": "40.69427000",
                  "longitude": "-73.91875000"
                },
                {
                  "id": 117935,
                  "name": "Hampton Bays",
                  "latitude": "40.86899000",
                  "longitude": "-72.51759000"
                },
                {
                  "id": 118023,
                  "name": "Harlem",
                  "latitude": "40.80788000",
                  "longitude": "-73.94542000"
                },
                {
                  "id": 121115,
                  "name": "Manhattan",
                  "latitude": "40.78343000",
                  "longitude": "-73.96625000"
                },
                {
                  "id": 122795,
                  "name": "New York City",
                  "latitude": "40.71427000",
                  "longitude": "-74.00597000"
                }
              ]
            }
          ]
        },
        {
          "id": 2,
          "name": "Vietnam",
          "iso2": "VN",
          "capital": "Hanoi",
          "currency": "VND",
          "currency_symbol": "đ",
          "latitude": "16.16666666",
          "longitude": "107.83333333",
          "states": [
            {
              "id": 3806,
              "name": "Đà Nẵng",
              "state_code": "DN",
              "latitude": "16.05440680",
              "longitude": "108.20216670",
              "type": null,
              "cities": [
                {
                  "id": 130195,
                  "name": "Da Nang",
                  "latitude": "16.06778000",
                  "longitude": "108.22083000"
                }
              ]
            },
            {
              "id": 3810,
              "name": "Hà Nội",
              "state_code": "HN",
              "latitude": "21.02776440",
              "longitude": "105.83415980",
              "type": null,
              "cities": [
                {
                  "id": 130201,
                  "name": "Hanoi",
                  "latitude": "21.02450000",
                  "longitude": "105.84117000"
                }
              ]
            },
            {
              "id": 3811,
              "name": "Hồ Chí Minh",
              "state_code": "SG",
              "latitude": "10.82309890",
              "longitude": "106.62966380",
              "type": null,
              "cities": [
                {
                  "id": 130191,
                  "name": "Cần Giờ",
                  "latitude": "10.41115000",
                  "longitude": "106.95474000"
                },
                {
                  "id": 130194,
                  "name": "Củ Chi",
                  "latitude": "10.97333000",
                  "longitude": "106.49325000"
                },
                {
                  "id": 130202,
                  "name": "Ho Chi Minh City",
                  "latitude": "10.82302000",
                  "longitude": "106.62965000"
                }
              ]
            },
            {
              "id": 3793,
              "name": "Khánh Hòa",
              "state_code": "34",
              "latitude": "12.25850980",
              "longitude": "109.05260760",
              "type": null,
              "cities": [
                {
                  "id": 130182,
                  "name": "Cam Ranh",
                  "latitude": "11.92144000",
                  "longitude": "109.15913000"
                },
                {
                  "id": 130576,
                  "name": "Nha Trang",
                  "latitude": "12.24507000",
                  "longitude": "109.19432000"
                },
                {
                  "id": 130598,
                  "name": "Thành Phố Cam Ranh",
                  "latitude": "11.90707000",
                  "longitude": "109.14861000"
                },
                {
                  "id": 130600,
                  "name": "Thành Phố Nha Trang",
                  "latitude": "12.25458000",
                  "longitude": "109.16655000"
                }
              ]
            },
            {
              "id": 3798,
              "name": "Thừa Thiên-Huế",
              "state_code": "26",
              "latitude": "16.46739700",
              "longitude": "107.59053260",
              "type": null,
              "cities": [
                {
                  "id": 130554,
                  "name": "Huế",
                  "latitude": "16.46190000",
                  "longitude": "107.59546000"
                }
              ]
            }
          ]
        }
    ];

    const handleButton = () => {
        if (!location.country) {
            setMessage({message: "Country must be provided.", status: "Error"});
        }
        else if (!location.state) {
            setMessage({message: "State/province must be provided.", status: "Error"});
        }
        else if (!location.city) {
            setMessage({message: "City/region must be provided.", status: "Error"});
        }
        else {
            if (popupType === "Add") {
                setMessage({message: "", status: ""});

                axios.post(`/user/${user?.id}/address`, location).then(res => {
                    setMessage({message: "Address added.", status: "Success"});
                    setPopupActive(false);
                }).catch(err => {
                    setMessage({message: err.response?.data?.message, status: "Error"});
                    setPopupActive(false);
                });
            }
            else if (popupType === "Edit") {
                axios.put(`/user/${user?.id}/address/${location.id}`, location).then(res => {
                    setMessage({message: "Address updated.", status: "Success"});
                    setPopupActive(false);
                }).catch(err => {
                    setMessage({message: err.response?.data?.message, status: "Error"});
                    setPopupActive(false);
                });
            }
        }
    }
    
    useEffect(() => {
        if (!popupActive) {
            document.body.style.overflow = "auto";
        }
        else {
            document.body.style.overflow = "hidden";
        }
    }, [popupActive]);

    return (
        <Layout title="Profile Overview">
            <div className="overview">
                <div className={"background " + (popupActive ? "active" : "")}></div>
                <div className={"popup " + (popupActive ? "active" : "")}>
                    <div className="title">
                        Add a new address
                    </div>
                    <div className="close" onClick={() => setPopupActive(false)}>x</div>
                    <div className="address-form">
                        <div className="form-group">
                            <div className="form-label">
                                Country<span>*</span>
                            </div>
                            <select 
                                className="form-select"
                                value={location.country}
                                defaultValue="Select country"
                                onChange={(e) => setLocation(prev => ({
                                    ...prev,
                                    country: e.target.value
                                }))}>
                                <option 
                                    className="form-option" 
                                    disabled={location.country ? true : false}
                                    value="Select country">
                                    Select country
                                </option>
                                {
                                    data.map(country => 
                                        <option 
                                            className="form-option" 
                                            value={country.name}
                                            key={country.id}>
                                            {country.name}
                                        </option>
                                    )
                                }
                            </select>
                        </div>
                        <div className="form-groups">
                            <div className="form-group double">
                                <div className="form-label">
                                    State/Province<span>*</span>
                                </div>
                                <select 
                                    className="form-select"
                                    defaultValue="Select state/province"
                                    onChange={(e) =>setLocation(prev => ({
                                        ...prev,
                                        state: e.target.value
                                    }))}
                                    value={location.state}>
                                    <option 
                                        className="form-option" 
                                        disabled={location.state ? true : false}
                                        value="Select state/province">
                                        Select state/province
                                    </option>
                                    {
                                        location.country &&
                                        data.map(country =>
                                            country.name === location.country &&
                                            country.states.map(state =>
                                                <option className="form-option">
                                                    {state.name}
                                                </option>
                                            )
                                        )
                                    }
                                </select>
                            </div>
                            <div className="form-group double">
                                <div className="form-label">
                                    City/Region<span>*</span>
                                </div>
                                <select
                                    className="form-select"
                                    defaultValue={"Select city/region"}
                                    value={location.city}
                                    onChange={(e) =>setLocation(prev => ({
                                        ...prev,
                                        city: e.target.value
                                    }))}>
                                    <option 
                                        className="form-option" 
                                        disabled={location.city ? true : false}
                                        value="Select city/region">
                                        Select city/region
                                    </option>
                                    {
                                        location.state &&
                                        data.map(country =>
                                            country.name === location.country &&
                                            country.states.map(state =>
                                                state.name === location.state &&
                                                state.cities.map(city =>
                                                    <option className="form-option">
                                                        {city.name}
                                                    </option>
                                                )
                                            )
                                        )
                                    }
                                </select>
                            </div>
                        </div>
                        <div className="form-group">
                            <div className="form-label">
                                Street
                            </div>
                            <input 
                                className="form-input" 
                                value={location.street}
                                onChange={(e) => setLocation(prev => ({
                                    ...prev,
                                    street: e.target.value
                                }))}
                                type="text" />
                        </div>
                        <div className="form-group">
                            <div className="form-label">
                                Building
                            </div>
                            <input 
                                className="form-input" 
                                value={location.building}
                                onChange={(e) => setLocation(prev => ({
                                    ...prev,
                                    building: e.target.value
                                }))}
                                type="text" />
                        </div>
                        <div className="form-group checkbox">
                            <input 
                                className="form-input" 
                                onChange={() => setLocation(prev => ({
                                    ...prev,
                                    default: true
                                }))}
                                checked={location.default}
                                type="checkbox" />
                            <div className="form-label">
                                Set default address
                            </div>
                        </div>
                    </div>
                    <div 
                        className={
                            "message " + 
                            (message.status !== "" ? 
                                message.status === "Success" ? "success" : "error"
                                : ""
                            )
                        }>{message.message}</div>
                    <div 
                        className="add-button" 
                        onClick={handleButton}>
                        {
                            popupType === "Add" ?
                            "Add address" : "Edit address"
                        }
                        
                    </div>
                </div>
                <div className="wrapper">
                    <div className="info">
                        <Link className="info-section" to="/dashboard">
                            <div className="label">
                                Your Account
                            </div>
                            <div className="arrow">
                                {">"}
                            </div>
                        </Link>
                        <div 
                            className={"info-subsection " + (currentOutlet && "active")}
                            onClick={onReturnToOverview}>
                            <div className="label">
                                Profile settings
                            </div>
                            <div className="arrow">
                                {">"}
                            </div>
                        </div>
                        {
                            currentOutlet &&
                            <div className="info-outlet">
                                <div className="label">
                                    {currentOutlet}
                                </div>
                            </div>
                        }
                    </div>
                    <div className="title">
                        <div className="name">
                            Profile settings
                        </div>
                        <div className="description">
                            Edit your personal and login information.
                        </div>
                    </div>
                    <div className="sections-nav">
                        {
                            sections.map((section, index) => 
                                <div 
                                    className={"section " + (currentOutlet === section.name && "active")} 
                                    onClick={() => onSelectSection(index)}>
                                    {section.name}
                                </div>
                            )
                        }
                    </div>
                    <Outlet 
                        context={{
                            popupActive: popupActive,
                            setCurrentOutlet: setCurrentOutlet,
                            setPopupActive: setPopupActive,
                            setLocation: setLocation,
                            setPopupType: setPopupType
                        } satisfies OutletContext} />
                </div>
            </div>
        </Layout>
    );
}