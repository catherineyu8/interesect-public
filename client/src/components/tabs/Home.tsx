import { useEffect, useState } from "react";
import DailyQuiz from "./DailyQuiz";
import Recs from "./Recs";
import Stats from "./Stats";
import Navbar from "../Navbar";
import { returningUser } from "../../utils/api";
import InitialQuiz from "../InitialQuiz";

/**
 * This is the component for the home page
 * @returns the HTML for the home page
 */
export default function Home() {
  //useState to see if it's a new or returning user
  const [userExists, setUserExists] = useState<String>("");

  useEffect(() => {
    //checking if user exists in database
    returningUser().then((data) => setUserExists(data.result));
  }, []);

  if (userExists === "true") {
    //if it's a returning user
    console.log(userExists);
    return (
      <div className="wrapper">
        <Navbar />
      </div>
    );
  } else if (userExists === "false") {
    //if it's a first time user
    console.log(userExists);
    return <InitialQuiz />;
  } else {
    //if we're waiting for the .then
    return <p>Loading...</p>;
  }
}
