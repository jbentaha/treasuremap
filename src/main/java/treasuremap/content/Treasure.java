package treasuremap.content;

import lombok.Getter;
import lombok.Setter;
import treasuremap.adventurer.MovableI;
import treasuremap.utils.TRMStringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class Treasure extends Element {

    @Getter
    private AtomicInteger nbTreasures = new AtomicInteger();

    public Treasure(int nbTreasures) {
        this.nbTreasures.set(nbTreasures);
    }

    public boolean decreaseNbTreasures() {
        if(nbTreasures.get() > 0) {
            nbTreasures.decrementAndGet();
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = getStringBuilder();
        MovableI movable = getMovable();

        if(nbTreasures.get() == 0 && movable == null) {
            sb.append(TRMStringUtils.DOT);
        }

        if(nbTreasures.get() > 0) {
            sb.append(TRMStringUtils.T).append(TRMStringUtils.OPENING_PARENTHESIS).append(nbTreasures).append(TRMStringUtils.CLOSING_PARENTHESIS);
        }

        if(movable != null) { // just in case a Treasure and adventurer end up in the same box
            sb.append(TRMStringUtils.A).append(TRMStringUtils.OPENING_PARENTHESIS).append(movable.getName()).append(TRMStringUtils.CLOSING_PARENTHESIS);
        }

        return sb.toString();
    }
}
