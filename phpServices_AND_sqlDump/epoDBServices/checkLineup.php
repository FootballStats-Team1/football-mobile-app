<?php
header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["has_lineup" => false]));
}

$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;
$count = 0;

if ($matchId > 0) {
    $stmt = $conn->prepare("SELECT COUNT(*) AS c FROM match_lineups WHERE match_id = ?");
    $stmt->bind_param("i", $matchId);
    $stmt->execute();
    $res = $stmt->get_result();
    $row = $res->fetch_assoc();
    $count = (int)$row['c'];
    $stmt->close();
}

echo json_encode([
    "match_id"   => $matchId,
    "count"      => $count,
    "has_lineup" => ($count > 0)
]);
$conn->close();
?>