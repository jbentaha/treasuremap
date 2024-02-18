package treasuremap;

import org.junit.jupiter.api.Test;
import treasuremap.game.TreasureRunner;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapEmptyTest extends TreasureRunner {

    private static final String INPUT_FILE_PATH = "src/test/resources/treasuremap-test-empty.txt";

    /*
    ensure the map throws an exception when rows and columns = 0
     */
    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, this::run);
    }

    @Override
    protected String getInputFilePath() {
        return INPUT_FILE_PATH;
    }

    @Override
    protected void printResult() {
        // do not print the result
    }
}
