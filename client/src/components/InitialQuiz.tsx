import { useState } from "react";
import "../styles/quiz.css";
import { Grid } from "@mui/material";
import { addUser } from "../utils/api";

/**
 * This is the componenent for the initial quiz for a first time user
 * @returns the HTML for the initial quiz page
 */
export default function InitialQuiz() {
  const [submitted, setSubmitted] = useState<boolean>(false);
  const [answer1, setAnswer1] = useState<string>("none");
  const [answer2, setAnswer2] = useState<string>("none");
  const [answer3, setAnswer3] = useState<string>("none");
  const [answer4, setAnswer4] = useState<string>("none");

  //handles when a user clicks the submit button at the end of the inital quiz
  function handleSubmit(): void {
    addUser(answer1, answer2, answer3, answer4); //add user api endpoint
    setSubmitted(true);
  }

  //handles when a user answers the first IQ question
  function handleFirstAnswer(
    answer: string
  ): import("react").MouseEventHandler<HTMLButtonElement> | undefined {
    return () => {
      console.log("first answer: " + answer);
      setAnswer1(answer);
    };
  }
  //handles when a user answers the second IQ question
  function handleSecondAnswer(
    answer: string
  ): import("react").MouseEventHandler<HTMLButtonElement> | undefined {
    return () => {
      console.log("second answer: " + answer);
      setAnswer2(answer);
    };
  }
  //handles when a user answers the third IQ question
  function handleThirdAnswer(
    answer: string
  ): import("react").MouseEventHandler<HTMLButtonElement> | undefined {
    return () => {
      console.log("third answer: " + answer);
      setAnswer3(answer);
    };
  }
  //handles when a user answers the fourth IQ question
  function handleFourthAnswer(
    answer: string
  ): import("react").MouseEventHandler<HTMLButtonElement> | undefined {
    return () => {
      console.log("fourth answer: " + answer);
      setAnswer4(answer);
    };
  }

  //if the user hasn't submitted the initial quiz, display the questions
  if (!submitted) {
    if (
      answer1 != "none" &&
      answer2 != "none" &&
      answer3 != "none" &&
      answer4 != "none"
    ) {
      return (
        <div>
          <h2 style={{ marginTop: "3%" }}>
            Thanks for making an account! Please fill out the form below to
            begin.
          </h2>

          <div className="question">
            <h3>What's your favorite music genre?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("r&b")}>R&B</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("pop")}>Pop</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("rap")}>Rap</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("rock")}>Rock</button>
                </Grid>
              </Grid>
            </div>
          </div>
          <div className="question">
            <h3>What's your favorite weekend activity?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("hiking")}>Hiking</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("art")}>Art</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("go to a museum")}>
                    Go to a museum
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("go out to dinner")}>
                    Go out to dinner
                  </button>
                </Grid>
              </Grid>
            </div>
          </div>
          <div className="question">
            <h3>What's your favorite sport to play?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("dancing")}>
                    Dancing
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("ice skating")}>
                    Ice Skating
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("running")}>
                    Running
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("rowing")}>Rowing</button>
                </Grid>
              </Grid>
            </div>
          </div>
          <div className="question">
            <h3>How would you describe your aesthetic?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("cottage core")}>
                    Cottage Core
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("minimalist")}>
                    Minimalist
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("vintage")}>
                    Vintage
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("y2k")}>Y2K</button>
                </Grid>
              </Grid>
            </div>
          </div>

          <div className="submit-button">
            <button onClick={handleSubmit}>Submit</button>
          </div>
        </div>
      );
    } else {
      return (
        <div>
          <h2 style={{ marginTop: "3%" }}>
            Thanks for making an account! Please fill out the form below to
            begin.
          </h2>

          <div className="question">
            <h3>What's your favorite music genre?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("r&b")}>R&B</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("pop")}>Pop</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("rap")}>Rap</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFirstAnswer("rock")}>Rock</button>
                </Grid>
              </Grid>
            </div>
          </div>
          <div className="question">
            <h3>What's your favorite weekend activity?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("hiking")}>Hiking</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("art")}>Art</button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("go to a museum")}>
                    Go to a museum
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleSecondAnswer("go out to dinner")}>
                    Go out to dinner
                  </button>
                </Grid>
              </Grid>
            </div>
          </div>
          <div className="question">
            <h3>What's your favorite sport to play?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("dancing")}>
                    Dancing
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("ice skating")}>
                    Ice Skating
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("running")}>
                    Running
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleThirdAnswer("rowing")}>Rowing</button>
                </Grid>
              </Grid>
            </div>
          </div>
          <div className="question">
            <h3>How would you describe your aesthetic?</h3>
            <div
              style={{
                alignContent: "center",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Grid container spacing={2}>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("cottage core")}>
                    Cottage Core
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("minimalist")}>
                    Minimalist
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("vintage")}>
                    Vintage
                  </button>
                </Grid>
                <Grid item xs={3}>
                  <button onClick={handleFourthAnswer("y2k")}>Y2K</button>
                </Grid>
              </Grid>
            </div>
          </div>
        </div>
      );
    }
  } else {
    //if a user has already submitted the initial quiz
    return (
      <p>
        Thanks for submitting your initial quiz! Come back tomorrow for more fun
        features :D
      </p>
    );
  }
}
