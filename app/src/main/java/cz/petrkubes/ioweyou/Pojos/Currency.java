package cz.petrkubes.ioweyou.Pojos;

/**
 * @author Petr Kubes
 */
public class Currency {

    public int id;
    public String symbol;

    public Currency(int id, String symbol) {
        this.id = id;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
