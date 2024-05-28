
import config from "../config/config";
import { Kafka } from "kafkajs";

const kafka = new Kafka({
    clientId: "email-service",
    brokers: [config.kafka.bootstrap_server!]
});

export default kafka;