// functions/src/index.ts
import { onRequest } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { setGlobalOptions } from "firebase-functions";
import axios from "axios";

setGlobalOptions({ maxInstances: 10 });

const OPENAI_API_KEY = defineSecret("OPENAI_API_KEY");

export const chat = onRequest(
  { secrets: [OPENAI_API_KEY] },
  async (req, res) => {
    try {
      if (req.method !== "POST") {
        res.status(405).send({ error: "Method Not Allowed. Use POST." });
        return;
      }

      const body = req.body;
      if (!body) {
        res.status(400).send({ error: "Missing JSON body" });
        return;
      }

      // Get secret value at runtime
      const apiKey = OPENAI_API_KEY.value();
      if (!apiKey) {
        res.status(500).send({ error: "OpenAI key not available" });
        return;
      }

      // Forward the exact body to OpenAI
      const openaiResp = await axios.post(
        "https://api.openai.com/v1/chat/completions",
        body,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${apiKey}`,
          },
          timeout: 30000,
        }
      );

      // Return OpenAI response object to client
      res.status(openaiResp.status).json(openaiResp.data);
    } catch (err: any) {
      console.error("chat function error:", err?.response?.data ?? err.message ?? err);
      // If axios error with response, forward that status and data
      if (err?.response) {
        res.status(err.response.status).json(err.response.data);
      } else {
        res.status(500).json({ error: err.message ?? "Unknown error" });
      }
    }
  }
);