import "./PersonalInfo.scss";
import { useAuth } from "../../../hooks/useAuth";
import { useEffect, useState } from "react";
import { User } from "../../../types/types";
import { useAxios } from "../../../hooks/useAxios";
import { getTrueDate } from "../../../util/getTrueDate";
import { useOutletContext } from "react-router-dom";
import { OutletContext } from "../ProfileOverview";

export const PersonalInfo = () => {
    const { user } = useAuth();
    const { axios } = useAxios();

    const context = useOutletContext<OutletContext>();

    const {roles, ...userInfo}: any = user;
    const [currentUserInfo, setCurrentUserInfo] = useState(userInfo);
    const [newUserInfo, setNewUserInfo] = useState(userInfo);

    const currentYear = (new Date()).getFullYear();

    const oldBirthDate = user?.birthDate ? new Date(user.birthDate) : new Date();
    const [newBirthDate, setNewBirthDate] = useState<{
        year: string,
        month: string,
        day: string
    }>({
        year: getTrueDate(oldBirthDate.getFullYear()),
        month: getTrueDate(oldBirthDate.getMonth() + 1),
        day: getTrueDate(oldBirthDate.getDate())
    });

    const formSections = [
        {
            id: "fullName",
            label: "Full name",
            value: "",
            canEdit: true,
            type: "text"
        },
        {
            id: "gender",
            label: "Gender",
            value: "",
            canEdit: true,
            type: "radio-button"
        },
        {
            id: "birthDate",
            label: "Birthdate",
            value: "",
            canEdit: true,
            type: "date"
        },
        {
            id: "mobile",
            label: "Mobile number",
            value: "",
            canEdit: true,
            type: "text"
        }
    ];
    const [activeInput, setActiveInput] = useState(
        Object.fromEntries(formSections.map(section =>
            [section.id, false]
        ))
    );
    const [updateStatus, setUpdateStatus] = useState(
        formSections.map(section =>
            ({id: section.id, code: 0, message: ""})
        )
    );

    const returnInput = (type: string, id: string) => {
        switch (type) {
            case "text":
                return (<input 
                            className={"form-input " + (activeInput[id] ? "active" : "")}
                            onChange={(e) => updateInput(e, id)} 
                            value={newUserInfo[id as keyof User]} 
                            type="text" />);
            case "radio-button":
                return (<div className={"form-radio " + (activeInput[id] ? "active" : "")}>
                            <div className="radio-group">
                                <div className="label">
                                    Male
                                </div>
                                <input 
                                    className="radio" 
                                    type="radio"
                                    name="gender"
                                    value="Male"
                                    onChange={(e) => updateInput(e, id)}
                                    defaultChecked={user ? user.gender === "Male" : false} />
                            </div>
                            <div className="radio-group">
                                <div className="label">
                                    Female
                                </div>
                                <input 
                                    className="radio" 
                                    type="radio"
                                    name="gender"
                                    value="Female" 
                                    onChange={(e) => updateInput(e, id)}
                                    defaultChecked={user ? user.gender === "Female" : false} />
                            </div>
                            <div className="radio-group">
                                <div className="label">
                                    Other
                                </div>
                                <input 
                                    className="radio" 
                                    type="radio" 
                                    name="gender"
                                    value="Other" 
                                    onChange={(e) => updateInput(e, id)}
                                    defaultChecked={user ? user.gender === "Other" : false} />
                            </div>
                        </div>);
            case "date":
                return <div className={"form-date " + (activeInput[id] ? "active" : "")}>
                            <div className="form-date-group">
                                <div className="label">
                                    Year
                                </div>
                                <select 
                                    className="select-input" 
                                    name="years" 
                                    onChange={(e) => updateBirthDate(e, "year")}>
                                    {
                                        getRange(currentYear - 18, currentYear - 100, -1).map(year => 
                                            <option 
                                                className="option"
                                                selected={year === parseInt(newBirthDate.year)}
                                                value={year}>
                                                {year}
                                            </option>
                                        )
                                    }
                                </select>
                            </div>
                            <div className="form-date-group">
                                <div className="label">
                                    Month
                                </div>
                                <select 
                                    className="select-input" 
                                    name="months"
                                    onChange={(e) => updateBirthDate(e, "month")}>
                                    {
                                        getRange(12, 1, -1).map(month => 
                                            <option 
                                                className="option" 
                                                selected={month === parseInt(newBirthDate.month)}
                                                value={month}>
                                                {month < 10 ? `0${month}` : month}
                                            </option>
                                        )
                                    }
                                </select>
                            </div>
                            <div className="form-date-group">
                                <div className="label">
                                    Day
                                </div>
                                <select 
                                    className="select-input" 
                                    name="days"
                                    onChange={(e) => updateBirthDate(e, "day")}>
                                    {
                                        getRange(getDaysInMonth(parseInt(newBirthDate.month), parseInt(newBirthDate.year)), 1, -1).map(day => 
                                            <option 
                                                className="option"
                                                selected={day === parseInt(newBirthDate.day)} 
                                                value={day}>
                                                {day < 10 ? `0${day}` : day}
                                            </option>
                                        )
                                    }
                                </select>
                            </div>    
                        </div>
            default:
                return <input className="form-input" type="text" />;
        }
    }

    const updateBirthDate = (e: React.ChangeEvent<HTMLSelectElement>, type: string) => {
        const value = parseInt(e.target.value);
        
        setNewBirthDate(prev => ({
            ...prev,
            [type]: value < 10 ? `0${value}` : String(value)
        }));
    }

    const updateInput = (e: React.ChangeEvent<HTMLInputElement>, id: string) => {
        // TODO: Validate input
        const info = newUserInfo;
        info[id] = e.target.value;

        setNewUserInfo({...info});
    }

    const toggleInput = (id: string) => {
        const newActiveInput = activeInput;
        newActiveInput[id] = !newActiveInput[id];

        setActiveInput({...newActiveInput});
        let userInfoRequest = newUserInfo;

        if (!newActiveInput[id]) {
            if (id === "birthDate") {
                userInfoRequest.birthDate = newBirthDate.year + 
                                        "-" + newBirthDate.month + 
                                        "-" + newBirthDate.day + 
                                        "T00:00:00";
            }

            const newUpdateStatus = [...updateStatus];

            axios.put(`/user/${user?.id}`, userInfoRequest).then(res => {
                newUpdateStatus.forEach(item => {
                    if (item.id === id) {
                        item.code = res.status;
                    }
                });

                setNewUserInfo({...userInfoRequest});
                setCurrentUserInfo({...userInfoRequest});
            }).catch(err => {
                newUpdateStatus.forEach(item => {
                    if (item.id === id) {
                        item.code = err.response.status;
                        item.message = err.response.data.message;
                    }
                });

                setNewUserInfo({...currentUserInfo});
            }).finally(() => {
                setUpdateStatus(newUpdateStatus);
            });
        }
    }

    const isError = (id: string) => {
        return updateStatus.filter(item => item.id === id)[0].code >= 400;
    }

    const isSuccess = (id: string) => {
        return updateStatus.filter(item => item.id === id)[0].code >= 200 && updateStatus.filter(item => item.id === id)[0].code < 400;
    }

    const getRange = (start: number, stop: number, step: number) => {
       return Array.from({length: (stop - start)/step + 1}, (_, i) => start + (i * step));
    }

    const getDaysInMonth = (month: number, year: number) => {
        if (month !== 0) {
            return new Date(year, month, 0).getDate();
        }

        return 31;
    }

    useEffect(() => {
        context.setCurrentOutlet("Personal information");
    }, []);

    return (
        <div className="personal-info">
            <div className="form">
                {
                    formSections.map(section => 
                        <div className="form-section" key={section.id}>
                            <div className="form-group">
                                <div className="form-label">
                                    <div className="name">
                                        {section.label}
                                    </div>
                                    <div 
                                        className={"error " + (isError(section.id) ? "active" : "")}>
                                        {
                                            updateStatus.filter(item => item.id === section.id)[0].message || 
                                            "Something went wrong."
                                        }
                                    </div>
                                    <div 
                                        className={"success " + (isSuccess(section.id) ? "active" : "")}>
                                        Updated.
                                    </div>
                                </div>
                                <div className={"form-value " + (activeInput[section.id] ? "" : "active")}>
                                    {user ? 
                                        section.id === "birthDate" ?
                                            (new Date(newUserInfo["birthDate"])
                                                .toLocaleDateString(
                                                    "en-US", 
                                                    {
                                                        dateStyle: "long"
                                                    }
                                                )
                                            ).toString() :
                                            newUserInfo[section.id as keyof User]
                                        : ""
                                    }
                                </div>
                                {
                                    returnInput(section.type, section.id)
                                }
                            </div>
                            {
                                section.canEdit &&
                                <div className="edit" onClick={() => toggleInput(section.id)}>
                                    {
                                        activeInput[section.id] ? "Save" : "Edit"
                                    }
                                </div>
                            } 
                        </div>
                    )
                }
            </div>
        </div>
    );
}