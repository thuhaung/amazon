import React from "react"

export type User = {
    id: string,
    email: string,
    fullName: string,
    birthDate: string,
    gender: string,
    mobile: string,
    isVerified: boolean,
    isActive: boolean,
    isVendor: boolean,
    roles: string[]
}

export type ReactChildren = {
    children: React.ReactNode
}

export type Credentials = {
    email: string,
    password: string
}

export type Timezone = {
    zoneName: string,
    gmtOffset: string,
    gmtOffsetName: string,
    abbreviation: string,
    tzName: string
}

export type Location = {
    id?: number,
    country: string,
    state: string,
    city: string,
    street: string,
    building: string,
    default: boolean
}

export type City = {
    id: number,
    name: string,
    latitude: string,
    longitude: string
}

export type State = {
    id: number,
    name: string,
    state_code: string,
    latitude: string,
    longitude: string,
    type: string | null,
    cities: City[]
}

export type Country = {
    id: number,
    name: string,
    iso2: string,
    capital: string,
    currency: string,
    currency_symbol: string,
    latitude: string,
    longitude: string,
    states: State[]
}