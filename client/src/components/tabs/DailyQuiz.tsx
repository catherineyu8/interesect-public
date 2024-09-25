import { useEffect, useState } from "react";
import { addDQAnswer, getDailyQuiz } from "../../utils/api";
import { getLoginCookie } from "../../utils/cookie";
import { Grid } from "@mui/material";
import "../../styles/quiz.css";

// TODO: keep track of some shared state for if user has completed daily quiz or not

/**
 * this is the component for the daily quiz
 * @returns HTML for the daily quiz
 */
export default function DailyQuiz() {
  const [question, setQuestion] = useState<String>("");
  const [answers, setAnswers] = useState<string[]>([]);

  const USER_ID = getLoginCookie() || "";

  //updates the daily quiz question
  useEffect(() => {
    getDailyQuiz().then((data) => {
      setQuestion(data.q);
      setAnswers(data.ansChoices);
    });
  }, []);

  //adds the user's DQ answer to the backend
  function handleAnswer(
    ans: string
  ): import("react").MouseEventHandler<HTMLButtonElement> | undefined {
    return () => {
      console.log("daily quiz answer: " + ans);
      addDQAnswer(ans);
    };
  }

  return (
    <div className="wrapper">
      {/* list of words from db: */}
      <p>
        <i aria-label="recs-header">Daily quiz for user {USER_ID}:</i>
      </p>

      <div className="question">
        {" "}
        {/*displaying question*/}
        <h3>{question}</h3>
        <div
          style={{
            alignContent: "center",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Grid container spacing={2}>
            {/*displaying answers*/}
            {answers.map((ans, index) => (
              <Grid key={index} item xs={1.7}>
                <button onClick={handleAnswer(ans)}>{ans}</button>
              </Grid>
            ))}
          </Grid>
        </div>
      </div>
    </div>
  );
}
