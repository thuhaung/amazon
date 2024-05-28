import { useContext } from "react"
import { AxiosContext } from "../context/AxiosContext"

export const useAxios = () => {
    return useContext(AxiosContext);
}