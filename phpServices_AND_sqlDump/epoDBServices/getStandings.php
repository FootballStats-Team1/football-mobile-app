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

// Query με JOIN για να πάρουμε ΚΑΙ το όνομα της ομάδας από τον πίνακα teams
$sql = "SELECT s.*, t.name AS team_name, t.badge AS logo_url 
		FROM standings s
		JOIN teams t ON s.team_id = t.id
		ORDER BY s.points DESC, (s.goals_for - s.goals_against) DESC";

$result = $conn->query($sql);

$data = array();

if ($result) {
    while ($row = $result->fetch_assoc()) {
        // Μετατρέπουμε τα νούμερα από Strings σε κανονικά Integers για να μην παιδευόμαστε στην Java
        $data[] = [
            "team_id"       => (int)$row['team_id'],
            "team_name"     => $row['team_name'],
			"logo_url" => $row['logo_url'],
            "points"        => (int)$row['points'],
            "wins"          => (int)$row['wins'],
            "draws"         => (int)$row['draws'],
            "losses"        => (int)$row['losses'],
            "goals_for"     => (int)$row['goals_for'],
            "goals_against" => (int)$row['goals_against']
        ];
    }
}

// Επιστροφή των δεδομένων σε JSON
echo json_encode($data, JSON_UNESCAPED_UNICODE);

$conn->close();
?>