export class Email {
    token: string;
    subject: string;
    recipient: string;
    body: string;
    timestamp: Date
}

export class EmailBuilder {
    private email: Email;

    constructor() {
        this.email = new Email();
    }

    token(token: string) {
        this.email.token = token;
        return this;
    }

    subject(subject: string) {
        this.email.subject = subject;
        return this;
    }

    recipient(recipient: string) {
        this.email.recipient = recipient;
        return this;
    }

    body(body: string) {
        this.email.body = body;
        return this;
    }

    build() {
        this.email.timestamp = new Date();
        return this.email;
    }
}