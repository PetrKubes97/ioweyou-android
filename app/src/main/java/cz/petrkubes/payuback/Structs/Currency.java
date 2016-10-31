package cz.petrkubes.payuback.Structs;

/**
 * Created by petr on 31.10.16.
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
