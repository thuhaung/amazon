import config from "./config/config";
import logger from "./loader/logger";
import runConsumer from "./service/kafka/consumer/consumer";
import app from "./loader/app";

const startApp = async () => {
    app.listen(config.port, () => {
        logger.info(`Server listening on port ${config.port}`);

        runConsumer().then(() => {
            logger.info("Consumer is running...");
        }).catch(error => {
            logger.error("Failed to run Kafka consumer", error);
        });
    }).on("error", error => {
        logger.error(`Failed to listen on port ${config.port}`, error);
        process.exit(1);
    });
}

startApp();