package edu.up.cs301.Mancala;

import java.util.ArrayList;

import edu.up.cs301.counter.CounterComputerPlayer1;
import edu.up.cs301.counter.CounterComputerPlayer2;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * this is the primary activity for Counter game
 *
 * @author Shayna Noone
 * @author Courtney Cox
 * @version Nov 11
 */
public class MancMainActivity extends GameMainActivity {

    // the port number that this game will use when playing over the network
    private static final int PORT_NUMBER = 2234;

    /**
     * Create the default configuration for this game:
     * - one human player vs. one computer player
     * - minimum of 1 player, maximum of 2
     * - one kind of computer player and one kind of human player available
     *
     * @return
     * 		the new configuration object, representing the default configuration
     */
    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // a human player player type (player type 0)
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new MancHumanPlayer(name);
            }});

        // a computer player type (player type 1)
        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new MancComputerPlayer1(name);
            }});

        // a computer player type (player type 2)
        playerTypes.add(new GamePlayerType("Computer Player (GUI)") {
            public GamePlayer createPlayer(String name) {
                return new MancComputerPlayer2(name);
            }});

        // Create a game configuration class for Manc:
        // - player types as given above
        // - from 1 to 2 players
        // - name of game is "Manc Game"
        // - port number as defined above
        GameConfig defaultConfig = new GameConfig(playerTypes, 2, 2, "Manc Game",
                PORT_NUMBER);

        // Add the default players to the configuration
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer", 1); // player 2: a computer player


        // Set the default remote-player setup:
        // - player name: "Remote Player"
        // - IP code: (empty string)
        // - default player type: human player
        defaultConfig.setRemoteData("Remote Player", "", 0);

        // return the configuration
        return defaultConfig;
    }//createDefaultConfig

    /**
     * create a local game
     *
     * @return
     * 		the local game, a counter game
     */
    @Override
    public LocalGame createLocalGame() {
        return new MancLocalGame();
    }

}
