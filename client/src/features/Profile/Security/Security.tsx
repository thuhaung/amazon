import "./Security.scss";
import { useState, useEffect } from "react";
import { useAuth } from "../../../hooks/useAuth";
import { useAxios } from "../../../hooks/useAxios";
import { useOutletContext } from "react-router-dom";
import { OutletContext } from "../ProfileOverview";

export const Security = () => {
    const { user } = useAuth();
    const { axios } = useAxios();

    const context = useOutletContext<OutletContext>();

    const {roles, ...userInfo}: any = user;
    const [currentUserInfo, setCurrentUserInfo] = useState({...userInfo});
    const [newUserInfo, setNewUserInfo] = useState({...userInfo});

    const [token, setToken] = useState<string>();

    const [passwordChange, setPasswordChange] = useState<{
        old: string,
        new: string,
        confirm: string
    }>({
        old: "",
        new: "",
        confirm: ""
    });

    const formSections = [
        {
            id: "email",
            label: "Email",
            value: "",
            canEdit: true,
            type: "text"
        },
        {
            id: "password",
            label: "Password",
            value: "",
            canEdit: true,
            type: "password"
        },
        {
            id: "isVerified",
            label: "Verified",
            value: "",
            canEdit: true,
            type: "radio"
        },
        {
            id: "isActive",
            label: "Deactivate",
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

    const isError = (id: string) => {
        return updateStatus.filter(item => item.id === id)[0].code >= 400;
    }

    const isSuccess = (id: string) => {
        return updateStatus.filter(item => item.id === id)[0].code >= 200 
                && updateStatus.filter(item => item.id === id)[0].code < 400;
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
            switch (id) {
                case "email":
                    {
                        const newUpdateStatus = [...updateStatus];

                        axios.put(`/user/${user?.id}`, userInfoRequest).then(res => {
                            newUpdateStatus.forEach(item => {
                                if (item.id === id) {
                                    item.code = res.status;
                                }
                            });
                            
                            userInfoRequest.isVerified = false;
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
                    break;
                case "password":
                    {
                        const newUpdateStatus = [...updateStatus];

                        if (Object.values(passwordChange).every(item => item !== "")) {
                            if (passwordChange.new !== passwordChange.confirm) {
                                newUpdateStatus.forEach(item => {
                                    if (item.id === id) {
                                        item.code = 400;
                                        item.message = "New password and confirm password don't match.";
                                    }
                                });

                                setUpdateStatus(newUpdateStatus);
                            }
                            else {
                                const changePasswordRequest = {
                                    email: user?.email,
                                    oldPassword: passwordChange.old,
                                    newPassword: passwordChange.new
                                }
    
                                axios.patch(`/user/${user?.id}/password`, changePasswordRequest).then(res => {
                                    newUpdateStatus.forEach(item => {
                                        if (item.id === id) {
                                            item.code = res.status;
                                        }
                                    });
                                }).catch(err => {
                                    newUpdateStatus.forEach(item => {
                                        if (item.id === id) {
                                            item.code = err.response.status;
                                            item.message = err.response.data.message;
                                        }
                                    });
                                }).finally(() => {
                                    setUpdateStatus(newUpdateStatus);
                                });
                            }
                        }
                        else if (Object.values(passwordChange).some(item => item !== "")) {
                            newUpdateStatus.forEach(item => {
                                if (item.id === id) {
                                    item.code = 400;
                                    item.message = "Fields cannot be empty.";
                                }
                            });

                            setUpdateStatus(newUpdateStatus);
                        }

                        setPasswordChange({
                            old: "",
                            new: "",
                            confirm: ""
                        });
                    }
                    break;
                case "isVerified":
                    {
                        const newUpdateStatus = [...updateStatus];

                        axios.post("/user/verify-email", {email: newUserInfo.email, token: token}).then(res => {
                            newUpdateStatus.forEach(item => {
                                if (item.id === id) {
                                    item.code = res.status;
                                    item.message = res.data;
                                }
                            });

                            userInfoRequest.isVerified = true;
                            setNewUserInfo({...userInfoRequest});
                        }).catch(err => {
                            newUpdateStatus.forEach(item => {
                                if (item.id === id) {
                                    item.code = err.response.status;
                                    item.message = err.response.data.message;
                                }
                            });
                        }).finally(() => {
                            setUpdateStatus(newUpdateStatus);
                        });
                    }

                    break;
            }
        }
        else {
            if (id === "isVerified") {
                const newUpdateStatus = [...updateStatus];

                axios.post("/user/send-verification", {email: newUserInfo.email}).then(res => {
                }).catch(err => {
                    newUpdateStatus.forEach(item => {
                        if (item.id === id) {
                            item.code = err.response.status;
                            item.message = err.response.data.message;
                        }
                    });
                }).finally(() => {
                    setUpdateStatus(newUpdateStatus);
                });
            }
        }
    }

    useEffect(() => {
        context.setCurrentOutlet("Security");
    }, []);

    return (
        <div className="security">
            <div className="form">
                <div className="form-section" id="email">
                    <div className="form-group">
                        <div className="form-label">
                            <div className="name">
                                Email
                            </div>
                            <div 
                                className={"error " + (isError("email") ? "active" : "")}>
                                {
                                    updateStatus.filter(item => item.id === "email")[0].message 
                                    || "Something went wrong."
                                }
                            </div>
                            <div 
                                className={"success " + (isSuccess("email") ? "active" : "")}>
                                Updated.
                            </div>
                        </div>
                        <div className={"form-value " + (activeInput.email ? "" : "active")}>
                            { newUserInfo.email }
                        </div>
                        <input 
                            className={"form-input " + (activeInput.email ? "active" : "")}
                            onChange={(e) => updateInput(e, "email")} 
                            value={newUserInfo.email} 
                            type="text" />
                    </div>
                    <div className="edit" onClick={() => toggleInput("email")}>
                        {
                            activeInput.email ? "Save" : "Edit"
                        }
                    </div>
                </div>
                {
                    newUserInfo && !newUserInfo.isVerified
                    &&
                    <div className="form-section" id="isVerified">
                        <div className="form-group">
                            <div className="form-label">
                                <div className="name">
                                    Email verification status
                                </div>
                                <div 
                                className={"error " + (isError("isVerified") ? "active" : "")}>
                                    {
                                        updateStatus.filter(item => item.id === "isVerified")[0].message 
                                        || "Something went wrong."
                                    }
                                </div>
                                <div 
                                    className={"success " + (isSuccess("isVerified") ? "active" : "")}>
                                    {
                                        updateStatus.filter(item => item.id === "isVerified")[0].message 
                                        || "Email is verified."
                                    }
                                </div>
                            </div>
                            {
                                !activeInput.isVerified ?
                                <div className="form-value active">
                                    {newUserInfo.isVerified ? "" : "Not verified."}
                                </div>
                                :
                                <div className="verify-by-email">
                                    <div className="message">
                                        A verification code has been sent to your email.
                                    </div>
                                    <div className="verify-section">
                                        <input 
                                            className="verify-input"
                                            onChange={(e) => setToken(e.target.value)} 
                                            type="text" />
                                    </div>
                                    <div className="verify" onClick={() => toggleInput("isVerified")}>
                                        Verify
                                    </div>
                                </div>
                            }
                        </div>
                        {
                            !activeInput.isVerified &&
                            <div className="edit" onClick={() => toggleInput("isVerified")}>
                                Update
                            </div>
                        }
                    </div>
                }
                <div className="form-section" id="password">
                    <div className="form-group">
                        {
                            !activeInput.password
                            && 
                            <div className="form-label">
                                <div className="name">
                                    Password
                                </div>
                                <div 
                                    className={"error " + (isError("password") ? "active" : "")}>
                                    {
                                        updateStatus.filter(item => item.id === "password")[0].message 
                                        || "Something went wrong."
                                    }
                                </div>
                                <div 
                                    className={"success " + (isSuccess("password") ? "active" : "")}>
                                    Updated.
                                </div>
                            </div>
                        }
                        <div className={"form-value " + (activeInput.password ? "" : "active")}>
                            *********
                        </div>
                        <div className={"edit-password " + (activeInput.password ? "active" : "")}>
                            <div className="edit-password-group">
                                <div className="edit-password-label">
                                    Current password
                                </div>
                                <input 
                                    className="edit-password-input"
                                    onChange={(e) => setPasswordChange(prev => ({
                                        ...prev,
                                        old: e.target.value
                                    }))} 
                                    value={passwordChange.old}
                                    type="password" />
                            </div>
                            <div className="edit-password-group">
                                <div className="edit-password-label">
                                    New password
                                </div>
                                <input 
                                    className="edit-password-input"
                                    onChange={(e) => setPasswordChange(prev => ({
                                        ...prev,
                                        new: e.target.value
                                    }))} 
                                    value={passwordChange.new}
                                    type="password" />
                            </div>
                            <div className="edit-password-group">
                                <div className="edit-password-label">
                                    Confirm new password
                                </div>
                                <input 
                                    className="edit-password-input"
                                    onChange={(e) => setPasswordChange(prev => ({
                                        ...prev,
                                        confirm: e.target.value
                                    }))}
                                    value={passwordChange.confirm}
                                    type="password" />
                            </div>
                        </div>
                    </div>
                    <div className="edit" onClick={() => toggleInput("password")}>
                        {
                            activeInput.password ? "Save" : "Edit"
                        }
                    </div>
                </div>
                <div className="form-section" id="isActive">
                    <div className="form-group">
                        <div className="form-label">
                            <div className="name">
                                Deactivate
                            </div>
                            <div 
                                className={"error " + (isError("isActive") ? "active" : "")}>
                                {
                                    updateStatus.filter(item => item.id === "isActive")[0].message 
                                    || "Something went wrong."
                                }
                            </div>
                            <div 
                                className={"success " + (isSuccess("isActive") ? "active" : "")}>
                                Deactivating.
                            </div>
                        </div>
                        {
                            !activeInput.isActive ?
                            <div className="form-value active">
                                Disable your account.
                            </div>
                            :
                            <div className="form-value active">
                                After disabling, if you do not log in for 30 days, your account will be deleted. Are you sure?
                            </div>
                        }
                    </div>
                    <div className="edit" onClick={() => toggleInput("isActive")}>
                        {
                            activeInput.isActive ? "Confirm" : "Edit"
                        }
                    </div>
                </div>
                {/* {
                    formSections.map(section => 
                        <div className="form-section" key={section.id}>
                            <div className="form-group">
                                <div className="form-label">
                                    <div className="name">
                                        {section.label}
                                    </div>
                                    <div 
                                        className={"error " + (isError(section.id) ? "active" : "")}>
                                        Something went wrong.
                                    </div>
                                    <div 
                                        className={"success " + (isSuccess(section.id) ? "active" : "")}>
                                        Updated.
                                    </div>
                                </div>
                                {
                                    section.id === "isVerified" ?
                                    (
                                        !newUserInfo[section.id] &&
                                        <div className={"form-value " + (activeInput[section.id] ? "" : "active")}>
                                            User not verified.
                                        </div>
                                    )
                                    :
                                    <div className={"form-value " + (activeInput[section.id] ? "" : "active")}>
                                        {newUserInfo[section.id]}
                                    </div>
                                }
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
                } */}
            </div>
        </div>
    );
}