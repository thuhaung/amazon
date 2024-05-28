export interface MailOptions {
    to: string,
    subject: string,
    text: string
}

export interface ErrorWithStatus extends Error {
    status?: number
}