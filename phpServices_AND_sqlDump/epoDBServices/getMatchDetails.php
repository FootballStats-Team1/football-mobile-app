<?php
// getMatchDetails.php
// Καλείται ως: getMatchDetails.php?matchId=121
// Επιστρέφει: τα βασικά στοιχεία ενός αγώνα (ομάδες, logos, σκορ, status)
//             + ΣΥΓΚΕΝΤΡΩΤΙΚΑ στατιστικά ομάδας (home/away) για τον αγώνα
// Τα logos δεν είναι στον πίνακα matches - είναι στον πίνακα teams (στήλη badge)
// Γι' αυτό κάνουμε 2 JOIN με τον teams: ένα για home, ένα για away
// Τα team stats ΔΕΝ υπάρχουν σαν πίνακας - τα υπολογίζουμε αθροίζοντας (SUM)
// τα προσωπικά στατιστικά των παικτών από τον πίνακα match_events

header('Content-Type: application/json; charset=utf-8');

// --- Σύνδεση με τη βάση ---
$host = "localhost";
$db   = "epodb";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["error" => "DB connection failed"]);
    exit;
}
$conn->set_charset("utf8mb4");

// --- Παίρνουμε το matchId από το GET ---
$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;
if ($matchId <= 0) {
    echo json_encode(["error" => "Invalid matchId"]);
    exit;
}

// =========================================================
// 1. ΒΑΣΙΚΑ ΣΤΟΙΧΕΙΑ ΑΓΩΝΑ
//    JOIN τον πίνακα teams 2 φορές με aliases (home / away)
//    home.badge  -> το logo του γηπεδούχου
//    away.badge  -> το logo του φιλοξενούμενου
// =========================================================
$sql = "
    SELECT 
        m.id              AS match_id,
        m.matchday        AS matchday,
        m.status          AS status,
        m.home_score      AS home_score,
        m.away_score      AS away_score,
        m.home_team_id    AS home_team_id,
        m.away_team_id    AS away_team_id,
        home.name         AS home_team,
        home.badge        AS home_logo,
        away.name         AS away_team,
        away.badge        AS away_logo
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

// SOS: Όπως στο getMatches.php, για αγώνες 'pending' στέλνουμε -1 σκορ
// ώστε το Match.getScoreText() στο Android να δείξει "- : -"
if ($row['status'] === 'pending') {
    $row['home_score'] = -1;
    $row['away_score'] = -1;
}

$homeTeamId = intval($row['home_team_id']);
$awayTeamId = intval($row['away_team_id']);

// =========================================================
// 2. ΣΥΓΚΕΝΤΡΩΤΙΚΑ ΣΤΑΤΙΣΤΙΚΑ ΟΜΑΔΑΣ (TEAM STATS)
//    Δεν υπάρχει πίνακας team_stats. Τα φτιάχνουμε αθροίζοντας (SUM)
//    τα στατιστικά ΟΛΩΝ των παικτών κάθε ομάδας σε αυτόν τον αγώνα.
//    JOIN: match_events (στατιστικά παικτών) -> players (για να ξέρουμε σε ποια ομάδα ανήκει ο παίκτης)
//    GROUP BY p.team_id: "μάζεψε" όλους τους παίκτες μιας ομάδας σε μία γραμμή με τα totals
// =========================================================
$sqlStats = "
    SELECT 
        p.team_id                  AS team_id,
        SUM(e.goals)               AS goals,
        SUM(e.shots_on_target)     AS shots_on_target,
        SUM(e.shots_off_target)    AS shots_off_target,
        SUM(e.passes_succ)         AS passes_succ,
        SUM(e.passes_fail)         AS passes_fail,
        SUM(e.tackles_succ)        AS tackles_succ,
        SUM(e.tackles_fail)        AS tackles_fail,
        SUM(e.crosses_succ)        AS crosses_succ,
        SUM(e.crosses_fail)        AS crosses_fail,
        SUM(e.assists)             AS assists,
        SUM(e.fouls_committed)     AS fouls_committed,
        SUM(e.fouls_won)           AS fouls_won,
        SUM(e.corners_won)         AS corners_won,
        SUM(e.yellow_cards)        AS yellow_cards,
        SUM(e.red_cards)           AS red_cards
    FROM match_events e
    JOIN players p ON e.player_id = p.id
    WHERE e.match_id = ?
    GROUP BY p.team_id
";

$stmt = $conn->prepare($sqlStats);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$statsResult = $stmt->get_result();

// Βοηθητική: φτιάχνει ένα "άδειο" stats object (όλα 0) - για αγώνες χωρίς events (π.χ. pending)
function emptyStats() {
    return [
        "goals" => 0, "shots_on_target" => 0, "shots_off_target" => 0,
        "passes_succ" => 0, "passes_fail" => 0,
        "tackles_succ" => 0, "tackles_fail" => 0,
        "crosses_succ" => 0, "crosses_fail" => 0,
        "assists" => 0, "fouls_committed" => 0, "fouls_won" => 0,
        "corners_won" => 0, "yellow_cards" => 0, "red_cards" => 0,
        "total_passes" => 0, "possession" => 0
    ];
}

$homeStats = emptyStats();
$awayStats = emptyStats();

// Πρώτα μαζεύουμε τα totals κάθε ομάδας σε προσωρινό πίνακα ανά team_id
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
        "fouls_committed"  => intval($s['fouls_committed']),
        "fouls_won"        => intval($s['fouls_won']),
        "corners_won"      => intval($s['corners_won']),
        "yellow_cards"     => intval($s['yellow_cards']),
        "red_cards"        => intval($s['red_cards']),
        "total_passes"     => intval($s['passes_succ']) + intval($s['passes_fail']),
        "possession"       => 0 // θα το υπολογίσουμε παρακάτω
    ];
}
$stmt->close();

// Αναθέτουμε στα home / away με βάση τα team ids του αγώνα
if (isset($statsByTeam[$homeTeamId])) {
    $homeStats = $statsByTeam[$homeTeamId];
}
if (isset($statsByTeam[$awayTeamId])) {
    $awayStats = $statsByTeam[$awayTeamId];
}

// --- Υπολογισμός κατοχής μπάλας (possession %) από τις συνολικές πάσες ---
// Possession ομάδας = (πάσες ομάδας / συνολικές πάσες αγώνα) * 100
$matchTotalPasses = $homeStats['total_passes'] + $awayStats['total_passes'];
if ($matchTotalPasses > 0) {
    $homeStats['possession'] = (int) round(($homeStats['total_passes'] * 100) / $matchTotalPasses);
    $awayStats['possession'] = 100 - $homeStats['possession']; // για να βγαίνει πάντα άθροισμα 100
}

// =========================================================
// 3. ΤΕΛΙΚΟ JSON (snake_case keys, ίδια λογική με getMatches.php)
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
    "home_stats" => $homeStats,  // συγκεντρωτικά στατιστικά γηπεδούχου
    "away_stats" => $awayStats   // συγκεντρωτικά στατιστικά φιλοξενούμενου
];

echo json_encode($response, JSON_UNESCAPED_UNICODE);

$conn->close();
?>
