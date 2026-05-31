<?php
// getMatchPlayerStats.php
// Ατομικά στατιστικά ΟΛΩΝ των παικτών για ΕΝΑΝ συγκεκριμένο αγώνα.
// Καλύπτει το R4 (στατιστικά παικτών κατά την εξέλιξη ΚΑΙ σε ολοκληρωμένο αγώνα)
// και το ατομικό κομμάτι του R5.
//
// ΣΗΜΑΝΤΙΚΟ: τα δεδομένα υπάρχουν ΗΔΗ στον πίνακα match_events
// (μία γραμμή ανά παίκτη ανά αγώνα) -> ΔΕΝ χρειάζεται καμία αλλαγή στη βάση.
// Είναι ακριβώς η ίδια λογική με το getMatchLineups.php (χωρισμός σε home/away),
// απλά αντί για ονόματα επιστρέφουμε τα στατιστικά κάθε παίκτη.
//
// Κλήση: getMatchPlayerStats.php?matchId=106

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

// JOIN: match_events (στατιστικά) -> players (ονομα/θεση/ομαδα) -> matches (ποια ειναι home/away)
$sql = "
    SELECT
        p.id AS player_id, p.name, p.position, p.team_id,
        m.home_team_id, m.away_team_id,
        e.goals, e.assists,
        e.shots_on_target, e.shots_off_target,
        e.passes_succ, e.passes_fail,
        e.tackles_succ, e.tackles_fail,
        e.crosses_succ, e.crosses_fail,
        e.errors, e.fouls_won, e.fouls_committed,
        e.corners_won, e.yellow_cards, e.red_cards
    FROM match_events e
    JOIN players p ON e.player_id = p.id
    JOIN matches m ON e.match_id  = m.id
    WHERE e.match_id = ?
    ORDER BY p.team_id, p.name ASC
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$res = $stmt->get_result();

$home = [];
$away = [];

while ($row = $res->fetch_assoc()) {
    $homeId = (int)$row['home_team_id'];

    $player = [
        "player_id"        => (int)$row['player_id'],
        "name"             => $row['name'],
        "position"         => $row['position'],
        "goals"            => (int)$row['goals'],
        "assists"          => (int)$row['assists'],
        "shots_on_target"  => (int)$row['shots_on_target'],
        "shots_off_target" => (int)$row['shots_off_target'],
        "passes_succ"      => (int)$row['passes_succ'],
        "passes_fail"      => (int)$row['passes_fail'],
        "tackles_succ"     => (int)$row['tackles_succ'],
        "tackles_fail"     => (int)$row['tackles_fail'],
        "crosses_succ"     => (int)$row['crosses_succ'],
        "crosses_fail"     => (int)$row['crosses_fail'],
        "errors"           => (int)$row['errors'],
        "fouls_won"        => (int)$row['fouls_won'],
        "fouls_committed"  => (int)$row['fouls_committed'],
        "corners_won"      => (int)$row['corners_won'],
        "yellow_cards"     => (int)$row['yellow_cards'],
        "red_cards"        => (int)$row['red_cards']
    ];

    // Χωρισμός σε γηπεδούχους / φιλοξενούμενους (όπως στο getMatchLineups.php)
    if ((int)$row['team_id'] === $homeId) {
        $home[] = $player;
    } else {
        $away[] = $player;
    }
}

echo json_encode([
    "match_id"     => $matchId,
    "home_players" => $home,
    "away_players" => $away
], JSON_UNESCAPED_UNICODE);

$stmt->close();
$conn->close();
?>
