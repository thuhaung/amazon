import mailer from "../../loader/mailer";
import { MailOptions } from "../../types/types";

export const sendVerificationEmail = async (options: MailOptions) => {
    mailer.sendMail({
        to: options.to,
        subject: options.subject,
        text: options.text
    });
}