

import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import dayjs from "dayjs";

admin.initializeApp();
const db = admin.firestore();


const REGION = "us-central1";
const LESSON_DEFAULT_XP = 10;
const STREAK_BADGES = [3, 7, 30];
const TODAY = () => dayjs().format("YYYY-MM-DD");


export const onProgressWrite = functions
  .region(REGION)
  .firestore
  .document("users/{uid}/progress/{docId}")
  .onWrite(async (change, context) => {
    const uid = context.params.uid as string;
    const after = change.after.exists ? change.after.data() : null;
    if (!after) return;


    if (after.status !== "completed") return;

    const progressRef = db.doc(`users/${uid}/progress/${context.params.docId}`);
    const userRef = db.doc(`users/${uid}`);

<<<<<<< Updated upstream
// Callable example (test from app)
export const ping = onCall({ region: "us-central1" }, async (req) => {
  if (!req.auth?.uid) throw new Error("Unauthenticated");
  return { ok: true, uid: req.auth.uid };
});
//test
=======
    await db.runTransaction(async (tx) => {
      const [progressSnap, userSnap] = await Promise.all([
        tx.get(progressRef),
        tx.get(userRef),
      ]);

      const progress = progressSnap.data() || {};
      const user = userSnap.data() || {};


      if (progress.finalized === true) {
        functions.logger.info(
          `Lesson already finalized for uid=${uid}, doc=${progressRef.id}`
        );
        return;
      }


      const xpAward =
        Number(progress.xpEarned ?? after.xpEarned ?? after.xpReward ?? LESSON_DEFAULT_XP) || 0;


      const today = TODAY();
      const last = (user.lastActiveDate as string | undefined) || "";
      let streak = Number(user.streak || 0);

      if (!last) {
        streak = 1;
      } else {
        const diff = dayjs(today).diff(dayjs(last), "day");
        if (diff === 0) {
        } else if (diff === 1) {
          streak += 1;
        } else {
          streak = 1;
        }
      }


      tx.set(
        userRef,
        {
          xpTotal: admin.firestore.FieldValue.increment(xpAward),
          lessonsCompletedCount: admin.firestore.FieldValue.increment(1),
          lastActiveDate: today,
          streak,
          updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        },
        { merge: true }
      );


      tx.set(
        progressRef,
        {
          finalized: true,
          xpEarned: xpAward,
          completedAt:
            progress.completedAt ?? admin.firestore.FieldValue.serverTimestamp(),
          updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        },
        { merge: true }
      );


      if (STREAK_BADGES.includes(streak)) {
        const code = `STREAK_${streak}`;
        const achRef = db.doc(`users/${uid}/achievements/${code}`);
        tx.set(
          achRef,
          {
            code,
            title: `${streak}-day Streak`,
            earnedAt: admin.firestore.FieldValue.serverTimestamp(),
            meta: { streak },
          },
          { merge: true }
        );
      }
    });

    functions.logger.info(
      `Finalized completion for uid=${uid}, progress=${progressRef.path}`
    );
  });


export const ping = functions
  .region(REGION)
  .https.onCall(async (_data, context) => {
    if (!context.auth?.uid) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User must be authenticated."
      );
    }
    return { ok: true, uid: context.auth.uid };
  });
>>>>>>> Stashed changes
