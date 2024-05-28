import { NextFunction, Request, Response } from "express";
import logger from "../loader/logger";
import { ErrorWithStatus } from "../types/types";

const errorHandler = (err: Error, req: Request, res: Response, next: NextFunction) => {
    logger.error(err);

    const status: number = (err as ErrorWithStatus).status || 500;
    const message: string = err.message || "An unexpected error occurred. Please try again later.";

    res.status(status).json({
        status: status,
        message: message
    });
}

export default errorHandler;