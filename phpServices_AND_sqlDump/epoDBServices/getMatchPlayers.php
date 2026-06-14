<?php
// getMatchPlayers.php?matchId=121
// Επιστρέφει ΟΛΟΥΣ τους παίκτες των 2 ομάδων ενός αγώνα (basic + subs),
// χωρισμένους σε home/away, ώστε ο υπεύθυνος στατιστικής να διαλέξει 11άδα.

header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;
if ($matchId <= 0) {
    die(json_encode(["error" => "Invalid matchId"]));
}

// 1. Βρίσκουμε τις 2 ομάδες του αγώνα + ονόματα/λογότυπα (JOIN teams 2 φορές)
$sqlMatch = "
    SELECT m.home_team_id, m.away_team_id,
           h.name AS home_team, h.badge AS home_logo,
           a.name AS away_team, a.badge AS away_logo
    FROM matches m
    JOIN teams h ON m.home_team_id = h.id
    JOIN teams a ON m.away_team_id = a.id
    WHERE m.id = ?
";
$stmt = $conn->prepare($sqlMatch);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$res = $stmt->get_result();
if ($res->num_rows === 0) {
    die(json_encode(["error" => "Match not found"]));
}
$matchRow = $res->fetch_assoc();
$homeTeamId = (int)$matchRow['home_team_id'];
$awayTeamId = (int)$matchRow['away_team_id'];
$stmt->close();

// 2. Παίρνουμε ΟΛΟΥΣ τους παίκτες των 2 ομάδων
$sqlPlayers = "
    SELECT id, name, position, team_id, photo
    FROM players
    WHERE team_id = ? OR team_id = ?
    ORDER BY team_id, id ASC
";
$stmt = $conn->prepare($sqlPlayers);
$stmt->bind_param("ii", $homeTeamId, $awayTeamId);
$stmt->execute();
$res = $stmt->get_result();

$home = [];
$away = [];
while ($row = $res->fetch_assoc()) {
    $player = [
        "player_id" => (int)$row['id'],
        "name"      => $row['name'],
        "position"  => $row['position'],
        // trim() γιατί μερικά photo URLs έχουν \n στην αρχή -> χαλάει η Picasso
        "photo"     => trim($row['photo'])
    ];
    if ((int)$row['team_id'] === $homeTeamId) {
        $home[] = $player;
    } else {
        $away[] = $player;
    }
}

echo json_encode([
    "match_id"     => $matchId,
    "home_team"    => $matchRow['home_team'],
    "home_logo"    => trim($matchRow['home_logo']),
    "away_team"    => $matchRow['away_team'],
    "away_logo"    => trim($matchRow['away_logo']),
    "home_players" => $home,
    "away_players" => $away
], JSON_UNESCAPED_UNICODE);

$stmt->close();
$conn->close();
?>