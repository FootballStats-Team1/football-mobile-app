package com.example.temp_project;

// Συγκεντρωτικά στατιστικά ΜΙΑΣ ομάδας για έναν αγώνα.
// Προέρχονται από SUM των προσωπικών στατιστικών (match_events) όλων των παικτών της ομάδας.
// Φτιάχνουμε 2 τέτοια objects ανά αγώνα: ένα για τον γηπεδούχο, ένα για τον φιλοξενούμενο.
public class MatchStats {
    private int goals;
    private int shotsOnTarget;
    private int shotsOffTarget;
    private int passesSucc;
    private int passesFail;
    private int tacklesSucc;
    private int tacklesFail;
    private int crossesSucc;
    private int crossesFail;
    private int assists;
    private int foulsCommitted;
    private int foulsWon;
    private int cornersWon;
    private int yellowCards;
    private int redCards;
    private int totalPasses; // passesSucc + passesFail (υπολογισμένο από το PHP)
    private int possession;  // % κατοχής μπάλας (υπολογισμένο από το PHP)

    // Constructor
    public MatchStats(int goals, int shotsOnTarget, int shotsOffTarget,
                      int passesSucc, int passesFail, int tacklesSucc, int tacklesFail,
                      int crossesSucc, int crossesFail, int assists,
                      int foulsCommitted, int foulsWon, int cornersWon,
                      int yellowCards, int redCards, int totalPasses, int possession) {
        this.goals = goals;
        this.shotsOnTarget = shotsOnTarget;
        this.shotsOffTarget = shotsOffTarget;
        this.passesSucc = passesSucc;
        this.passesFail = passesFail;
        this.tacklesSucc = tacklesSucc;
        this.tacklesFail = tacklesFail;
        this.crossesSucc = crossesSucc;
        this.crossesFail = crossesFail;
        this.assists = assists;
        this.foulsCommitted = foulsCommitted;
        this.foulsWon = foulsWon;
        this.cornersWon = cornersWon;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.totalPasses = totalPasses;
        this.possession = possession;
    }

    // --- Βοηθητικές μέθοδοι για το UI ---

    // Συνολικές προσπάθειες (σουτ εντός + εκτός)
    public int getTotalShots() {
        return shotsOnTarget + shotsOffTarget;
    }

    // Ποσοστό ευστοχίας πάσας ως κείμενο (π.χ. "85%")
    public String getPassAccuracyText() {
        if (totalPasses == 0) {
            return "0%";
        }
        int pct = Math.round((passesSucc * 100f) / totalPasses);
        return pct + "%";
    }

    // Κατοχή ως κείμενο (π.χ. "55%")
    public String getPossessionText() {
        return possession + "%";
    }

    // --- Getters ---
    public int getGoals() { return goals; }
    public int getShotsOnTarget() { return shotsOnTarget; }
    public int getShotsOffTarget() { return shotsOffTarget; }
    public int getPassesSucc() { return passesSucc; }
    public int getTacklesSucc() { return tacklesSucc; }
    public int getCornersWon() { return cornersWon; }
    public int getFoulsCommitted() { return foulsCommitted; }
    public int getYellowCards() { return yellowCards; }
    public int getRedCards() { return redCards; }
    public int getTotalPasses() { return totalPasses; }
    public int getPossession() { return possession; }

    /* NOT (YET) USED GETTERS
    public int getPassesFail() { return passesFail; }
    public int getTacklesFail() { return tacklesFail; }
    public int getCrossesSucc() { return crossesSucc; }
    public int getCrossesFail() { return crossesFail; }
    public int getAssists() { return assists; }
    public int getFoulsWon() { return foulsWon; } */
}
