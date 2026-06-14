<?php
header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");

$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;

// Φέρνουμε τους παίκτες αυτού του αγώνα και κοιτάμε αν ανήκουν στη γηπεδούχο ή φιλοξενούμενη ομάδα
$sql = "SELECT p.id, p.name, m.home_team_id, m.away_team_id, p.team_id
        FROM match_lineups ml
        JOIN players p ON ml.player_id = p.id
        JOIN matches m ON ml.match_id = m.id
        WHERE ml.match_id = $matchId";

$result = $conn->query($sql);

$home_players = [];
$away_players = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $playerStr = $row['id'] . ": " . $row['name'];
        if ($row['team_id'] == $row['home_team_id']) {
            $home_players[] = $playerStr;
        } else {
            $away_players[] = $playerStr;
        }
    }
}

echo json_encode([
    "home_players" => $home_players,
    "away_players" => $away_players
]);
?>