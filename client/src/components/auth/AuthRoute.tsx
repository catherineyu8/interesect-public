import { useState } from "react";
import LoginLogout from "./LoginLogout";
import InitialQuiz from "../InitialQuiz";
import Home from "../tabs/Home";

function AuthRoute() {
  const [authing, setAuthing] = useState(false);

  // USEFUL FOR PLAYWRIGHT TESTING PURPOSES: auto sets authing to true in test environment
  if (!authing && import.meta.env.VITE_APP_NODE_ENV === "test") {
    setAuthing(true);
  }

  return (
    <>
      <LoginLogout authing={authing} setAuthing={setAuthing} />
      {authing ? <Home /> : null}
    </>
  );
}

export default AuthRoute;
