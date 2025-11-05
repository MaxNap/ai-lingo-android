import * as admin from "firebase-admin";
import { onDocumentWritten } from "firebase-functions/v2/firestore";
import { onCall } from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";

admin.initializeApp();
const db = admin.firestore();

// Triggered when a progress doc changes to completed
export const onProgressWrite = onDocumentWritten(
  { document: "users/{uid}/progress/{docId}", region: "us-central1" },
  async (event) => {
    const uid = event.params.uid as string;
    const after = event.data?.after?.data();
    if (!after || after.status !== "completed") return;
    logger.info(`Lesson completed by ${uid}`);

    // Example: set a timestamp if missing (idempotent)
    const userRef = db.doc(`users/${uid}`);
    await userRef.set({ lastActiveDate: new Date().toISOString().slice(0,10) }, { merge: true });
  }
);

// Callable example (test from app)
export const ping = onCall({ region: "us-central1" }, async (req) => {
  if (!req.auth?.uid) throw new Error("Unauthenticated");
  return { ok: true, uid: req.auth.uid };
});
