<?php
header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

// Για κάθε αγωνιστική: πόσοι αγώνες συνολικά, πόσοι finished, πόσοι live
$sql = "
    SELECT matchday,
           COUNT(*)                  AS total,
           SUM(status = 'finished')  AS finished,
           SUM(status = 'live')      AS live
    FROM matches
    GROUP BY matchday
    ORDER BY matchday ASC
";

$result = $conn->query($sql);
$data = array();

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $total    = (int)$row['total'];
        $finished = (int)$row['finished'];
        $live     = (int)$row['live'];

        $status = "pending";
        if ($finished === $total) {
            $status = "finished";
        } else if ($live > 0) {
            $status = "live";
        }

        $data[] = [
            "matchday" => (int)$row['matchday'],
            "status"   => $status
        ];
    }
}

echo json_encode($data, JSON_UNESCAPED_UNICODE);
$conn->close();
?>