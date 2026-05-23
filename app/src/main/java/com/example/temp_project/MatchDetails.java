package com.example.temp_project;

// Container που κρατάει ΟΛΑ τα δεδομένα της οθόνης λεπτομερειών αγώνα:
// - τον ίδιο τον αγώνα (Match)
// - τα συγκεντρωτικά στατιστικά του γηπεδούχου (MatchStats)
// - τα συγκεντρωτικά στατιστικά του φιλοξενούμενου (MatchStats)
public class MatchDetails {
    private Match match;
    private MatchStats homeStats;
    private MatchStats awayStats;

    // --- Constructor 1: παίρνει το matchId και κατεβάζει τα δεδομένα (ίδιο pattern με MatchesList) ---
    public MatchDetails(int matchId) {
        String url = Config.BASE_URL + "getMatchDetails.php?matchId=" + matchId;
        // ΠΡΟΣΟΧΗ: η υπηρεσία παίρνει ως GET attribute το matchId (π.χ. 'getMatchDetails.php?matchId=121')

        try {
            OkHttpHandler okHttpHandler = new OkHttpHandler();
            // Ο handler επιστρέφει έτοιμο ένα MatchDetails - "αντιγράφουμε" τα περιεχόμενά του
            MatchDetails details = okHttpHandler.getMatchDetails(url);
            this.match = details.getMatch();
            this.homeStats = details.getHomeStats();
            this.awayStats = details.getAwayStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Constructor 2: τον χρησιμοποιεί ο OkHttpHandler για να φτιάξει το object αφού κάνει parse ---
    public MatchDetails(Match match, MatchStats homeStats, MatchStats awayStats) {
        this.match = match;
        this.homeStats = homeStats;
        this.awayStats = awayStats;
    }

    // --- Getters: τα καλεί η MatchDetailsActivity ---
    public Match getMatch() {
        return match;
    }

    public MatchStats getHomeStats() {
        return homeStats;
    }

    public MatchStats getAwayStats() {
        return awayStats;
    }
}
