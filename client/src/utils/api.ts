import { GeoPoint } from "firebase/firestore";
import { getLoginCookie } from "./cookie";

const HOST = "http://localhost:3232";

/**
 * this makes queries to the backend
 * @param endpoint is the endpoint to query on the backend
 * @param query_params are the parameters to pass into the endpoitn
 * @returns
 */
async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

export async function getRecs() {
  return await queryAPI("list-recs", {
    uid: getLoginCookie() || "",
  });
}

export async function getDailyQuiz() {
  // TODO: randomly get one of the apis, add it as a parameter
  return await queryAPI("get-DQ", {});
}

export async function getStats() {
  return await queryAPI("list-stats", {
    uid: getLoginCookie() || "",
  });
}

export async function getAnswers() {
  return await queryAPI("list-iq-answers", {
    uid: getLoginCookie() || "",
  });
}

export async function returningUser() {
  return await queryAPI("returning-user", {
    uid: getLoginCookie() || "",
  });
}

export async function addUser(
  ans1: string,
  ans2: string,
  ans3: string,
  ans4: string
) {
  console.log("adding user");
  return await queryAPI("add-new-user", {
    uid: getLoginCookie() || "",
    inita1: ans1 || "",
    inita2: ans2 || "",
    inita3: ans3 || "",
    inita4: ans4 || "",
  });
}

//TODO:
export async function addDQAnswer(a: string) {
  return await queryAPI("add-DQ-answer", {
    uid: getLoginCookie() || "",
    answer: a,
  });
}

/****************
 * TODO: replace these functions with whatever we need
 ****************/

// export async function addWord(word: string) {
//   return await queryAPI("add-word", {
//     uid: getLoginCookie() || "",
//     word: word,
//   });
// }

// export async function addPin(pinLat: string, pinLong: string) {
//   return await queryAPI("add-pin", {
//     uid: getLoginCookie() || "",
//     pinLat: pinLat,
//     pinLong: pinLong,
//   });
// }

// export async function clearUser(uid: string = getLoginCookie() || "") {
//   return await queryAPI("clear-user", {
//     uid: uid,
//   });
// }

// export async function searchAreaQuery(query: string) {
//   return await queryAPI("searchRedline", { searchTarget: query });
// }
