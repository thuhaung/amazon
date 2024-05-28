import { Helmet } from "react-helmet";

export const Head = ({title = "", description = ""}) => {
    return (
        <Helmet
            title={title ? title : "Amazon App"}>
            <meta name="description" content={description} />
        </Helmet>
    )
}