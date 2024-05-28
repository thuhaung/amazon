import dotenv from "dotenv";

process.env.NODE_ENV = process.env.NODE_ENV || "development";
const env = dotenv.config();

if (env.error) {
    throw new Error("Cannot find .env file.");
}

const config = {
    port: process.env.PORT,

    // winston
    logs: {
        level: process.env.LOG_LEVEL || "info"
    },

    // api
    api: {
        prefix: "/mail"
    },

    smtp: {
        host: process.env.SMTP_HOST,
        port: process.env.SMTP_PORT,
        username: process.env.SMTP_USERNAME,
        password: process.env.SMTP_PASSWORD
    },

    kafka: {
        bootstrap_server: process.env.KAFKA_BOOTSTRAP_SERVER,
        group_id: process.env.KAFKA_CONSUMER_GROUP_ID,
        verification_topic: process.env.KAFKA_EMAIL_VERIFICATION_TOPIC
    }
};

export default config;