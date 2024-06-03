package main.dataModels;

public enum Capitals {
    AGRIGENTO("Agrigento"),
    ANCONA("Ancona"),
    AOSTA("Aosta"),
    BARI("Bari"),
    BOLOGNA("Bologna"),
    CAGLIARI("Cagliari"),
    CAMPOBASSO("Campobasso"),
    CATANZARO("Catanzaro"),
    FIRENZE("Firenze"),
    GENOVA("Genova"),
    LAQUILA("L'Aquila"),
    MILANO("Milano"),
    NAPOLI("Napoli"),
    PALERMO("Palermo"),
    PERUGIA("Perugia"),
    POTENZA("Potenza"),
    ROMA("Roma"),
    TORINO("Torino"),
    TRENTO("Trento"),
    TRIESTE("Trieste"),
    VENEZIA("Venezia");

    private final String capital;

    Capitals(String capital) {
        this.capital = capital;
    }

    public String getCapital() {
        return this.capital;
    }

    @Override
    public String toString() {
        return this.capital;
    }

    public static boolean isValidCapital(String city) {
        for (Capitals capital : Capitals.values()) {
            if (capital.getCapital().equalsIgnoreCase(city)) {
                return true;
            }
        }
        return false;
    }
}
