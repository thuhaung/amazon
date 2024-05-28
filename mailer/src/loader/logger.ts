import winston from "winston";
import config from "../config/config";

const { combine, timestamp, printf, colorize, align } = winston.format;

const logger = winston.createLogger({
    level: config.logs.level,
    levels: winston.config.npm.levels,
    format: combine(
        colorize({ all: true }),
        timestamp({
            format: "YYYY-MM-DD hh:mm:ss.SSS A"
        }),
        align(),
        printf((info) => 
            `[${info.timestamp}] ${info.level}: ${info.message}`
        )
    ),
    transports: [new winston.transports.Console()]
});

export default logger;
