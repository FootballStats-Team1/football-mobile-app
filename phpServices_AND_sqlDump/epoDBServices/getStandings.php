<?php
header('Content-Type: application/json; charset=utf-8');

$host = "localhost";
$uname = "root";
$pass = "";
$dbname = "epodb";

$conn = new mysqli($host, $uname, $pass, $dbname);
$conn->set_charset("utf8mb4");

if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

// =========================================================
//  ΔΕΝ υπάρχει πλέον πίνακας standings.
//  Υπολογίζουμε τη βαθμολογία ON-THE-FLY από τους τελειωμένους αγώνες.
//
//  Λογική:
//   - Κάθε αγώνας με status='finished' δίνει 2 εγγραφές: μία για τη
//     γηπεδούχο (gf=home_score, ga=away_score) και μία για τη
//     φιλοξενούμενη (gf=away_score, ga=home_score).
//   - Τις ενώνουμε με UNION ALL σε έναν "εικονικό" πίνακα g(team_id, gf, ga).
//   - LEFT JOIN με τον teams ώστε να εμφανίζονται ΚΑΙ ομάδες χωρίς αγώνες (όλα 0).
//   - Νίκη = 3 βαθμοί, Ισοπαλία = 1, Ήττα = 0.
//
//  Σημείωση: μετράμε ΜΟΝΟ status='finished'. Αν θες να μετρούν και οι
//  'live', άλλαξε το WHERE σε:  WHERE status IN ('finished','live')
// =========================================================
$sql = "
    SELECT
        t.id    AS team_id,
        t.name  AS team_name,
        t.badge AS logo_url,
        COUNT(g.team_id) AS played,
        SUM(CASE WHEN g.gf > g.ga THEN 1 ELSE 0 END) AS wins,
        SUM(CASE WHEN g.gf = g.ga THEN 1 ELSE 0 END) AS draws,
        SUM(CASE WHEN g.gf < g.ga THEN 1 ELSE 0 END) AS losses,
        COALESCE(SUM(g.gf), 0) AS goals_for,
        COALESCE(SUM(g.ga), 0) AS goals_against,
        COALESCE(SUM(g.gf) - SUM(g.ga), 0) AS goal_difference,
        SUM(CASE WHEN g.gf > g.ga THEN 3
                 WHEN g.gf = g.ga THEN 1
                 ELSE 0 END) AS points
    FROM teams t
    LEFT JOIN (
        SELECT home_team_id AS team_id, home_score AS gf, away_score AS ga
        FROM matches WHERE status = 'finished'
        UNION ALL
        SELECT away_team_id AS team_id, away_score AS gf, home_score AS ga
        FROM matches WHERE status = 'finished'
    ) g ON g.team_id = t.id
    GROUP BY t.id, t.name, t.badge
    ORDER BY points DESC, goal_difference DESC, goals_for DESC, team_name ASC
";

$result = $conn->query($sql);

$data = array();
if ($result) {
    while ($row = $result->fetch_assoc()) {
        // Μετατροπή σε Integers (όπως στο παλιό service) για να μην παιδεύεσαι στη Java
        $data[] = [
            "team_id"         => (int)$row['team_id'],
            "team_name"       => $row['team_name'],
            "logo_url"        => $row['logo_url'],
            "points"          => (int)$row['points'],
            "wins"            => (int)$row['wins'],
            "draws"           => (int)$row['draws'],
            "losses"          => (int)$row['losses'],
            "goals_for"       => (int)$row['goals_for'],
            "goals_against"   => (int)$row['goals_against'],
            // --- bonus πεδία (το παλιό service δεν τα είχε, δεν χαλάνε το app) ---
            "played"          => (int)$row['played'],
            "goal_difference" => (int)$row['goal_difference']
        ];
    }
}

echo json_encode($data, JSON_UNESCAPED_UNICODE);
$conn->close();
?>
