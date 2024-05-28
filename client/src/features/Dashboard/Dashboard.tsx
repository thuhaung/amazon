import "./Dashboard.scss";
import { Link, Outlet } from "react-router-dom";
import { Layout } from "../../components/Layout/Layout";

export const Dashboard = () => {
    const categories = [
        {
            id: "security",
            name: "Login & security",
            description: "Edit email, name, mobile number and addresses",
            link: "/dashboard/profile",
            image: "https://m.media-amazon.com/images/G/01/x-locale/cs/help/images/gateway/self-service/security._CB659600413_.png"
        },
        {
            id: "orders",
            name: "Your orders",
            description: "Track, return or cancel orders",
            link: "/",
            image: "https://m.media-amazon.com/images/G/01/x-locale/cs/help/images/gateway/self-service/order._CB660668735_.png"
        },
        {
            id: "lists",
            name: "Your lists",
            description: "View, modify, share or create new lists",
            link: "/",
            image: "https://m.media-amazon.com/images/G/01/x-locale/cs/help/images/gateway/self-service/fshub/11_lists._CB654640573_.png"
        },
        {
            id: "messages",
            name: "Your messages",
            description: "View or respond to sellers and buyers",
            link: "/",
            image: "https://m.media-amazon.com/images/G/01/x-locale/cs/help/images/gateway/self-service/fshub/9_messages._CB654640573_.jpg"
        },
        {
            id: "business-account",
            name: "Your business account",
            description: "Manage your products and orders",
            link: "/",
            image: "https://m.media-amazon.com/images/G/01/AmazonBusiness/YAPATF/amazon_business_yap_atf._CB588250197_.jpg"
        }
    ];

    return (
        <Layout title="Your Account">
            <div className="management">
                <div className="title">
                    Your account
                </div>
                <div className="categories">
                    {
                        categories.map(category => 
                            <Link className="category" key={category.id} to={category.link}>
                                <div className="illustration">
                                    <img
                                        src={category.image}
                                        alt={category.name} />
                                </div>
                                <div className="info">
                                    <div className="name">
                                        {category.name}
                                    </div>
                                    <div className="description">
                                        {category.description}
                                    </div>
                                </div>
                            </Link>
                        )
                    }
                </div>
            </div>
        </Layout>
    );
}