import { useEffect, useState } from "react";
import { getRecs } from "../../utils/api";
import { getLoginCookie } from "../../utils/cookie";

/**
 * This is the componenent for displaying recommendations
 * @returns HTML for the recommendations section
 */
export default function Recs() {
	const [recs, setRecs] = useState<String[][]>([]); // TODO: change the type we are using here

	const USER_ID = getLoginCookie() || "";

	useEffect(() => {
		getRecs().then((data) => {
			setRecs(data.recs); // TODO: make sure server response map contains a field "recs":ArrayList<String[]>
		});
	}, []);

	return (
		<div className="wrapper" style={{ color: "black" }}>
			{/* list of words from db: */}
			<p>
				<i aria-label="recs-header">
					Recommendations for user {USER_ID}:
				</i>
			</p>
			{/*displaying recommendations*/}
			<ul aria-label="recs">
				{recs.map((rec, index) => (
					<div key={index}>
						<p>
							Recommendation: {rec[0]} recommended on {rec[1]}.
						</p>
					</div>
				))}
			</ul>
		</div>
	);
}
