export const getFirstName = (fullName: string) => {
    const firstName = fullName.split(" ")[0];

    return firstName.charAt(0).toUpperCase() + firstName.slice(1, 8) + (firstName.length > 7 ? "..." : "");
}