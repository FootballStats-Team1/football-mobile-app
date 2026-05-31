<?php
// getPlayerStats.php
// Συγκεντρωτικά (σεζόν) στατιστικά παίκτη/παικτών, υπολογισμένα από τον match_events.
// ΔΕΝ χρειάζεται ξεχωριστός πίνακας player_stats: τα αθροίζουμε live με SUM + GROUP BY.
//
// Σημείωση: ΔΕΝ υπάρχει corners εδώ - τα κόρνερ έγιναν team-level (πίνακας matches).
//
// Κλήσεις:
//   getPlayerStats.php?playerId=77   -> totals ΕΝΟΣ παίκτη
//   getPlayerStats.php?teamId=3      -> totals όλων των παικτών ΜΙΑΣ ομάδας
//   getPlayerStats.php               -> totals ΟΛΩΝ των παικτών (π.χ. πίνακας σκόρερ)

header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("127.0.0.1", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

$playerId = isset($_GET['playerId']) ? intval($_GET['playerId']) : 0;
$teamId   = isset($_GET['teamId'])   ? intval($_GET['teamId'])   : 0;

// Δυναμικό φίλτρο με prepared statement (ασφάλεια από SQL injection)
$where  = "WHERE m.status = 'finished'";
$types  = "";
$params = [];
if ($playerId > 0) { $where .= " AND p.id = ?";      $types .= "i"; $params[] = $playerId; }
if ($teamId   > 0) { $where .= " AND p.team_id = ?"; $types .= "i"; $params[] = $teamId; }

$sql = "
    SELECT
        p.id       AS player_id,
        p.name     AS player_name,
        p.position AS position,
        p.team_id  AS team_id,
        COUNT(DISTINCT e.match_id) AS matches_played,
        SUM(e.goals)            AS goals,
        SUM(e.assists)          AS assists,
        SUM(e.shots_on_target)  AS shots_on_target,
        SUM(e.shots_off_target) AS shots_off_target,
        SUM(e.passes_succ)      AS passes_succ,
        SUM(e.passes_fail)      AS passes_fail,
        SUM(e.tackles_succ)     AS tackles_succ,
        SUM(e.tackles_fail)     AS tackles_fail,
        SUM(e.crosses_succ)     AS crosses_succ,
        SUM(e.crosses_fail)     AS crosses_fail,
        SUM(e.errors)           AS errors,
        SUM(e.fouls_won)        AS fouls_won,
        SUM(e.fouls_committed)  AS fouls_committed,
        SUM(e.yellow_cards)     AS yellow_cards,
        SUM(e.red_cards)        AS red_cards
    FROM match_events e
    JOIN players p ON e.player_id = p.id
    JOIN matches m ON e.match_id  = m.id
    $where
    GROUP BY p.id, p.name, p.position, p.team_id
    ORDER BY goals DESC, assists DESC, player_name ASC
";

$stmt = $conn->prepare($sql);
if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}
$stmt->execute();
$res = $stmt->get_result();

$data = [];
while ($row = $res->fetch_assoc()) {
    foreach ($row as $k => $v) {
        $row[$k] = ($k === 'player_name' || $k === 'position') ? $v : (int)$v;
    }
    $data[] = $row;
}

echo json_encode($data, JSON_UNESCAPED_UNICODE);
$stmt->close();
$conn->close();
?>
