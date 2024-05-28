import { type EachMessagePayload } from "kafkajs";
import kafka from "../../../loader/kafka";
import logger from "../../../loader/logger";
import { MailOptions } from "../../../types/types";
import config from "../../../config/config";
import { sendVerificationEmail } from "../../mailer/sendVerificationEmail";

const consumer = kafka.consumer({groupId: config.kafka.group_id!});

const handleMessage = async ({topic, partition, message}: EachMessagePayload) => {
    const payload = JSON.parse(message.value?.toString() || "");

    const options: MailOptions = {
        to: payload?.recipient,
        subject: payload?.subject,
        text: payload?.text
    }

    sendVerificationEmail(options);

    logger.info(`Sent email to ${payload?.recipient}`);

    await consumer.commitOffsets([{topic, partition, offset: (Number(message.offset) + 1).toString()}]);
}

const runConsumer = async () => {
    await consumer.connect();
    await consumer.subscribe({topic: config.kafka.verification_topic!});

    await consumer.run({
        autoCommit: false,
        eachMessage: handleMessage
    });
}

export default runConsumer;