<?php
// getTopStats.php
// Πίνακες κατάταξης παικτών (leaderboards) για ΟΛΟ το πρωτάθλημα.
// Καλύπτει το ατομικό κομμάτι του R5: Top σκόρερ, Top ασίστ, κάρτες.
//
// Κλήση:
//   getTopStats.php?stat=goals
//   getTopStats.php?stat=assists
//   getTopStats.php?stat=yellow_cards
//   getTopStats.php?stat=red_cards
//   (προαιρετικά)  &limit=10   -> πόσους παίκτες να επιστρέψει (default 20)
//
// Λογική: αθροίζουμε (SUM) το αντίστοιχο στατιστικό από ΟΛΑ τα match_events
// κάθε παίκτη και κάνουμε JOIN με players + teams για όνομα/φωτό/ομάδα/σήμα.
// Είναι ακριβώς η ίδια λογική με τα SUM στο getMatchDetails.php, απλά εδώ
// ομαδοποιούμε ανά ΠΑΙΚΤΗ (όχι ανά ομάδα) και για όλο το πρωτάθλημα.

header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

// --- 1. Διαβάζουμε & ΕΛΕΓΧΟΥΜΕ το stat (whitelist) ---
// ΠΡΟΣΟΧΗ: το όνομα της στήλης ΔΕΝ μπορεί να μπει σε prepared statement
// (το bind_param δένει ΤΙΜΕΣ, όχι ονόματα στηλών). Γι' αυτό επιτρέπουμε
// ΜΟΝΟ συγκεκριμένες τιμές. Χωρίς αυτό το whitelist -> SQL injection.
$allowed = ['goals', 'assists', 'yellow_cards', 'red_cards'];
$stat = isset($_GET['stat']) ? $_GET['stat'] : 'goals';
if (!in_array($stat, $allowed, true)) {
    die(json_encode([
        "error" => "Invalid stat. Use one of: " . implode(", ", $allowed)
    ]));
}

// --- 2. Όριο αποτελεσμάτων (top N) ---
$limit = isset($_GET['limit']) ? intval($_GET['limit']) : 7;
if ($limit <= 0) $limit = 7;

// --- 3. Query ---
// Το $stat είναι ΑΣΦΑΛΕΣ εδώ γιατί πέρασε από το whitelist παραπάνω.
// HAVING total > 0  -> δείχνουμε μόνο όσους έχουν αυτό το στατιστικό.
// ORDER BY total DESC, name ASC -> κατάταξη, με αλφαβητικό tie-break.
$sql = "
    SELECT
        p.id          AS player_id,
        p.name        AS name,
        p.position    AS position,
        p.photo       AS photo,
        t.id          AS team_id,
        t.name        AS team_name,
        t.badge       AS badge,
        SUM(e.$stat)  AS total
    FROM match_events e
    JOIN players p ON e.player_id = p.id
    JOIN teams   t ON p.team_id   = t.id
    GROUP BY p.id
    HAVING total > 0
    ORDER BY total DESC, p.name ASC
    LIMIT $limit
";

$result = $conn->query($sql);
$data = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        // Generic "total" key -> το ίδιο Android model δουλεύει και για τις 4 λίστες
        $data[] = [
            "player_id" => (int)$row['player_id'],
            "name"      => $row['name'],
            "position"  => $row['position'],
            "photo"     => $row['photo'],
            "team_id"   => (int)$row['team_id'],
            "team_name" => $row['team_name'],
            "badge"     => $row['badge'],
            "total"     => (int)$row['total']
        ];
    }
}

echo json_encode($data, JSON_UNESCAPED_UNICODE);
$conn->close();
?>