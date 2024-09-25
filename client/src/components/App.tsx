import { initializeApp } from "firebase/app";
import "../styles/App.css";
import AuthRoute from "./auth/AuthRoute";
import DailyQuiz from "./tabs/DailyQuiz";
import Recs from "./tabs/Recs";
import Stats from "./tabs/Stats";
import Home from "./tabs/Home";

const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};

initializeApp(firebaseConfig);
/**
 * This is the highest level component!
 */
function App() {
  return (
    <div className="App">
      <p className="App-header">
        <h1>Interesect</h1>
      </p>
      <AuthRoute />
    </div>
  );
}

export default App;
