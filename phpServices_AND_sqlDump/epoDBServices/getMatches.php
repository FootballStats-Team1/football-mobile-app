<?php
header("Content-Type: application/json; charset=UTF-8");

$host = "localhost";
$username = "root";
$password = "";
$dbname = "epodb"; 

$conn = new mysqli($host, $username, $password, $dbname);
$conn->set_charset("utf8");

if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

// Παίρνουμε υποχρεωτικά την αγωνιστική από το URL (π.χ. getMatches.php?matchday=1)
// Αν για κάποιο λόγο δεν σταλεί, βάζουμε προεπιλογή την 1η αγωνιστική
$matchday = isset($_GET['matchday']) ? intval($_GET['matchday']) : 1;

$sql = "SELECT m.id AS match_id, m.matchday, 
               t1.name AS home_team, t1.badge AS home_logo, 
               t2.name AS away_team, t2.badge AS away_logo, 
               m.home_score, m.away_score, m.status
        FROM matches m
        JOIN teams t1 ON m.home_team_id = t1.id
        JOIN teams t2 ON m.away_team_id = t2.id
        WHERE m.matchday = $matchday
        ORDER BY m.id ASC";

$result = $conn->query($sql);
$matches = array();

if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $matches[] = [
            "match_id" => intval($row['match_id']),
            "matchday" => intval($row['matchday']),
            "home_team" => $row['home_team'],
            "home_logo" => $row['home_logo'],
            "away_team" => $row['away_team'],
            "away_logo" => $row['away_logo'],
			// SOS - If the match hasn't started yet send -1, else send the scores normally
            "home_score" => ($row['status'] === 'pending') ? -1 : intval($row['home_score']),
            "away_score" => ($row['status'] === 'pending') ? -1 : intval($row['away_score']),
            "status" => $row['status']
        ];
    }
}

echo json_encode($matches, JSON_UNESCAPED_UNICODE);
$conn->close();
?>