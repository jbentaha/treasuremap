package treasuremap.content;

import treasuremap.adventurer.Adventurer;
import treasuremap.adventurer.MovableI;
import treasuremap.utils.TRMStringUtils;

public class Plain extends Element {

    @Override
    public String toString() {
        StringBuilder sb = getStringBuilder();

        MovableI movable = getMovable();
        if(movable != null) {
            sb.append(TRMStringUtils.A).append(TRMStringUtils.OPENING_PARENTHESIS).append(movable.getName()).append(TRMStringUtils.CLOSING_PARENTHESIS);
            return sb.toString();
        }

        return TRMStringUtils.DOT;
    }

}
