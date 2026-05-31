<?php
// getMatchDetails.php
// Καλείται ως: getMatchDetails.php?matchId=121
// Επιστρέφει: βασικά στοιχεία αγώνα + ΣΥΓΚΕΝΤΡΩΤΙΚΑ team stats (home/away)
//
// ΑΛΛΑΓΕΣ σε σχέση με την παλιά έκδοση:
//  - Τα ΚΟΡΝΕΡ είναι πλέον team-level και αποθηκεύονται στον πίνακα matches
//    (home_corners / away_corners), γιατί το UI τα καταγράφει ανά ΟΜΑΔΑ.
//    Δεν αθροίζονται πλέον από τον match_events.
//  - Όλα τα υπόλοιπα team stats παραμένουν DERIVED: αθροίζονται (SUM) από τα
//    προσωπικά στατιστικά των παικτών στον match_events (single source of truth).
//  - Προστέθηκε και το SUM(e.errors) που έλειπε από την παλιά έκδοση.

header('Content-Type: application/json; charset=utf-8');

$host = "127.0.0.1";   // 127.0.0.1 αντί για localhost -> σταθερή TCP σύνδεση στα Windows
$db   = "epodb";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["error" => "DB connection failed"]);
    exit;
}
$conn->set_charset("utf8mb4");

$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;
if ($matchId <= 0) {
    echo json_encode(["error" => "Invalid matchId"]);
    exit;
}

// =========================================================
// 1. ΒΑΣΙΚΑ ΣΤΟΙΧΕΙΑ ΑΓΩΝΑ (+ τα κόρνερ, που είναι πλέον εδώ)
// =========================================================
$sql = "
    SELECT
        m.id           AS match_id,
        m.matchday     AS matchday,
        m.status       AS status,
        m.home_score   AS home_score,
        m.away_score   AS away_score,
        m.home_corners AS home_corners,
        m.away_corners AS away_corners,
        m.home_team_id AS home_team_id,
        m.away_team_id AS away_team_id,
        home.name      AS home_team,
        home.badge     AS home_logo,
        away.name      AS away_team,
        away.badge     AS away_logo
    FROM matches m
    JOIN teams home ON m.home_team_id = home.id
    JOIN teams away ON m.away_team_id = away.id
    WHERE m.id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["error" => "Match not found"]);
    exit;
}

$row = $result->fetch_assoc();
$stmt->close();

// pending -> σκορ -1 (ώστε το Android να δείξει "- : -")
if ($row['status'] === 'pending') {
    $row['home_score'] = -1;
    $row['away_score'] = -1;
}

$homeTeamId = intval($row['home_team_id']);
$awayTeamId = intval($row['away_team_id']);

// =========================================================
// 2. DERIVED TEAM STATS (SUM από match_events) - ΧΩΡΙΣ κόρνερ
// =========================================================
$sqlStats = "
    SELECT
        p.team_id               AS team_id,
        SUM(e.goals)            AS goals,
        SUM(e.shots_on_target)  AS shots_on_target,
        SUM(e.shots_off_target) AS shots_off_target,
        SUM(e.passes_succ)      AS passes_succ,
        SUM(e.passes_fail)      AS passes_fail,
        SUM(e.tackles_succ)     AS tackles_succ,
        SUM(e.tackles_fail)     AS tackles_fail,
        SUM(e.crosses_succ)     AS crosses_succ,
        SUM(e.crosses_fail)     AS crosses_fail,
        SUM(e.assists)          AS assists,
        SUM(e.errors)           AS errors,
        SUM(e.fouls_committed)  AS fouls_committed,
        SUM(e.fouls_won)        AS fouls_won,
        SUM(e.yellow_cards)     AS yellow_cards,
        SUM(e.red_cards)        AS red_cards
    FROM match_events e
    JOIN players p ON e.player_id = p.id
    WHERE e.match_id = ?
    GROUP BY p.team_id
";

$stmt = $conn->prepare($sqlStats);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$statsResult = $stmt->get_result();

// "άδειο" stats object (όλα 0) - για αγώνες χωρίς events (π.χ. pending)
function emptyStats() {
    return [
        "goals" => 0, "shots_on_target" => 0, "shots_off_target" => 0,
        "passes_succ" => 0, "passes_fail" => 0,
        "tackles_succ" => 0, "tackles_fail" => 0,
        "crosses_succ" => 0, "crosses_fail" => 0,
        "assists" => 0, "errors" => 0,
        "fouls_committed" => 0, "fouls_won" => 0,
        "corners_won" => 0,            // team-level: μπαίνει από τον matches
        "yellow_cards" => 0, "red_cards" => 0,
        "total_passes" => 0, "possession" => 0
    ];
}

$homeStats = emptyStats();
$awayStats = emptyStats();

$statsByTeam = [];
while ($s = $statsResult->fetch_assoc()) {
    $tid = intval($s['team_id']);
    $statsByTeam[$tid] = [
        "goals"            => intval($s['goals']),
        "shots_on_target"  => intval($s['shots_on_target']),
        "shots_off_target" => intval($s['shots_off_target']),
        "passes_succ"      => intval($s['passes_succ']),
        "passes_fail"      => intval($s['passes_fail']),
        "tackles_succ"     => intval($s['tackles_succ']),
        "tackles_fail"     => intval($s['tackles_fail']),
        "crosses_succ"     => intval($s['crosses_succ']),
        "crosses_fail"     => intval($s['crosses_fail']),
        "assists"          => intval($s['assists']),
        "errors"           => intval($s['errors']),
        "fouls_committed"  => intval($s['fouls_committed']),
        "fouls_won"        => intval($s['fouls_won']),
        "corners_won"      => 0,  // μπαίνει από τον matches παρακάτω
        "yellow_cards"     => intval($s['yellow_cards']),
        "red_cards"        => intval($s['red_cards']),
        "total_passes"     => intval($s['passes_succ']) + intval($s['passes_fail']),
        "possession"       => 0
    ];
}
$stmt->close();

if (isset($statsByTeam[$homeTeamId])) $homeStats = $statsByTeam[$homeTeamId];
if (isset($statsByTeam[$awayTeamId])) $awayStats = $statsByTeam[$awayTeamId];

// --- ΚΟΡΝΕΡ: team-level, κατευθείαν από τον πίνακα matches ---
// home_corners = κόρνερ που ΚΕΡΔΙΣΕ ο γηπεδούχος, away_corners = ο φιλοξενούμενος.
$homeStats['corners_won'] = intval($row['home_corners']);
$awayStats['corners_won'] = intval($row['away_corners']);

// --- Κατοχή μπάλας (possession %) από τις συνολικές πάσες ---
$matchTotalPasses = $homeStats['total_passes'] + $awayStats['total_passes'];
if ($matchTotalPasses > 0) {
    $homeStats['possession'] = (int) round(($homeStats['total_passes'] * 100) / $matchTotalPasses);
    $awayStats['possession'] = 100 - $homeStats['possession'];
}

// =========================================================
// 3. ΤΕΛΙΚΟ JSON
// =========================================================
$response = [
    "match_id"   => intval($row['match_id']),
    "matchday"   => intval($row['matchday']),
    "home_team"  => $row['home_team'],
    "home_logo"  => $row['home_logo'],
    "away_team"  => $row['away_team'],
    "away_logo"  => $row['away_logo'],
    "home_score" => intval($row['home_score']),
    "away_score" => intval($row['away_score']),
    "status"     => $row['status'],
    "home_stats" => $homeStats,
    "away_stats" => $awayStats
];

echo json_encode($response, JSON_UNESCAPED_UNICODE);
$conn->close();
?>
