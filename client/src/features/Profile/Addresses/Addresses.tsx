import "./Addresses.scss";
import { useEffect, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { useAxios } from "../../../hooks/useAxios";
import { useAuth } from "../../../hooks/useAuth";
import { OutletContext } from "../ProfileOverview";
import { Location } from "../../../types/types";

export const Addresses = () => {
    const { user } = useAuth();
    const { axios } = useAxios();
    const [addresses, setAddresses] = useState<Location[]>([]);

    const context = useOutletContext<OutletContext>();

    const onEdit = (index: number) => {
        context.setLocation(addresses[index]);
        context.setPopupType("Edit");
        context.setPopupActive(true);
    }

    const addAddress = () => {
        context.setLocation({
            country: "",
            state: "",
            city: "",
            street: "",
            building: "",
            default: false
        });
        context.setPopupType("Add");
        context.setPopupActive(true);
    }

    const removeAddress = (index: number) => {
        const address = addresses[index];

        axios.delete(`/user/${user?.id}/address/${address.id}`).then(res => {
            addresses.splice(index, 1);
            const prev = addresses;

            setAddresses([...sortAddresses(prev)]);
        }).catch(err => {
            console.log(err);
        });
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

    useEffect(() => {
        context.setCurrentOutlet("Addresses");

        axios.get(`/user/${user?.id}/address`).then(res => {
            const response = res.data.reverse();
            
            // let index = response.findIndex((item: any) => item.default);

            // if (index > 0) {
            //     let temp = response[index];
            //     response[index] = response[0];
            //     response[0] = temp;
            // }

            setAddresses([...sortAddresses(response)]);
        }).catch(err => {
            console.log(err);
        });
    }, [context.popupActive]);

    return (
        <div className="listing">
            <div className="addresses">
                <div className="add address" onClick={addAddress}>
                    <div className="icon">+</div>
                    <div className="title">
                        Add address
                    </div>
                </div>
                {
                    addresses && addresses.map((address, index) => 
                        <div className="address" key={address.id}>
                            <div className="is-default">
                                {address.default ? "Default" : ""}
                            </div>
                            <div className="content">
                                <div className="fields">
                                    <div className="field building">
                                        {address.building}
                                    </div>
                                    <div className="field street">
                                        {address.street}
                                    </div>
                                    <div className="field city">
                                        {address.city}
                                    </div>
                                    <div className="field state">
                                        {address.state}
                                    </div>
                                    <div className="field country">
                                        {address.country}
                                    </div>
                                </div>
                                <div className="actions">
                                    <div 
                                        className="action edit" 
                                        onClick={() => onEdit(index)}>
                                        Edit
                                    </div>
                                    <div className="line"></div>
                                    <div 
                                        className="action remove"
                                        onClick={() => removeAddress(index)}>
                                        Remove
                                    </div>
                                </div>
                            </div>
                        </div>
                    )
                }
            </div>
        </div>
    );
}