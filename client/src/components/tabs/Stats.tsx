import { useEffect, useState } from "react";
import { getAnswers, getRecs, getStats } from "../../utils/api";
import { getLoginCookie } from "../../utils/cookie";

/**
 * This displays the statstics component of the site
 * @returns HTML for the statistics page
 */
export default function Stats() {
  const [stats, setStats] = useState<String[]>([]);
  const [answers, setAnswers] = useState<String[]>([]);

  const USER_ID = getLoginCookie() || "";

  //getting stats from backend
  useEffect(() => {
    getStats().then((data) => {
      setStats(data.stats);
    });
  }, []);

  //getting answers from backend
  useEffect(() => {
    getAnswers().then((data) => {
      setAnswers(data.answers);
    });
  }, []);

  return (
    <section id="stats">
      <div className="wrapper" id="Stats">
        {/* list of words from db: */}
        <p>
          <i aria-label="stats-header">Statistics for user {USER_ID}:</i>
        </p>
        {/*displaying stats*/}
        <ul aria-label="stats">
          <p>
            {stats[0]} of users also chose
            {" " + answers[0]} as their favorite music genre
          </p>
          <p>
            {stats[1]} of users also chose
            {" " + answers[1]} as their favorite weekend activity
          </p>
          <p>
            {stats[2]} of users also chose
            {" " + answers[2]} as their favorite sport
          </p>
          <p>
            {stats[3]} of users also chose {" " + answers[3]} as their favorite
            aesthetic
          </p>
        </ul>
      </div>
    </section>
  );
}
