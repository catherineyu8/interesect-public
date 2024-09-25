import { useState } from "react";
import Stats from "./tabs/Stats";
import Recs from "./tabs/Recs";
import DailyQuiz from "./tabs/DailyQuiz";
import "../styles/Navbar.css";

enum Section {
  STATS = "STATS",
  RECS = "RECS",
  DAILYQUIZ = "DAILYQUIZ",
}

/**
 * This is the component for the nav bar at the top of the page
 * @returns the HTML for the navigation bar
 */
export default function Navbar() {
  //use state for changing section based on what part of the nav bar user clicks on
  const [section, setSection] = useState<Section>(Section.DAILYQUIZ);

  return (
    <div>
      <div className="horizontal-navbar">
        <button onClick={() => setSection(Section.STATS)}>Statistics</button>
        <button onClick={() => setSection(Section.DAILYQUIZ)}>
          Daily Quiz
        </button>
        <button onClick={() => setSection(Section.RECS)}>
          Recommendations
        </button>
      </div>
      {section === Section.STATS ? <Stats /> : null}
      {section === Section.DAILYQUIZ ? <DailyQuiz /> : null}
      {section === Section.RECS ? <Recs /> : null}
    </div>
  );
}
