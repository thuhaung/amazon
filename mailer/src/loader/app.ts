import express from "express";
import cors from "cors";
import errorHandler from "../middleware/errorHandler";

const app = express();

// enables CORS
app.use(cors());

// parses json
app.use(express.json());

// error handling middleware
app.use(errorHandler);


export default app;
