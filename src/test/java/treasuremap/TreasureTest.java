package treasuremap;

import org.junit.jupiter.api.Test;
import treasuremap.adventurer.Adventurer;
import treasuremap.content.Treasure;
import treasuremap.game.TreasureRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

public class TreasureTest extends TreasureRunner {

    private static final String INPUT_FILE_PATH = "src/test/resources/treasuremap-test-treasure.txt";

    /*
    ensure adventurer consumes a treasure
     */
    @Test
    public void testTreasure() throws IOException {
        run();

        Treasure element = (Treasure) map.getElements()[3][0];

        assertEquals(1, element.getNbTreasures() ,"Should only have 1 treasure left");
        assertNotNull(element.getMovable(),"Should have an adventurer");
        assertEquals(1, ((Adventurer)element.getMovable()).getNbTreasures() ,"Should have collected 1 treasure");
    }

    @Override
    protected String getInputFilePath() {
        return INPUT_FILE_PATH;
    }

    @Override
    protected void printResult(){
        // do not print the result
    }
}
