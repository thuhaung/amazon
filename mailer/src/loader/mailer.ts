import * as nodemailer from "nodemailer";
import config from "../config/config";

const mailer = nodemailer.createTransport({
    host: config.smtp.host,
    port: config.smtp.port,
    auth: {
        user: config.smtp.username,
        pass: config.smtp.password
    },
    secure: true
} as nodemailer.TransportOptions);

export default mailer;