package edu.up.cs301.Mancala;

import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;


import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;


/**
 * A computer-version of a Mancala-player.  This computer player Determines if selection of any
 * of the six “holes” will result in an additional move
 *
 *1. Go through it’s array of holes
 *
 *2. Determine number of marbles in each hole
 *
 *3. If marbles equals distance from bank select this hole
 *
 *Determines if selection of any of the six “holes” will result in capturing opponents marbles
 *
 *1. Go through it’s array of holes
 *
 *2. Determine number of marbles in each hole
 *
 *3. Determine where the last marble will land
 *
 *4. Check the hole where marble lands
 *
 *5. If that hole is empty and belongs to player select the move.
 *
 *Determines which hole has the most marbles and selects that hole
 *
 *If no special move exists then randomly select one of the six “holes”. This computer
 *player does, however, have an option to display the game as it is progressing, so if
 *there is no human player on the device, this player will display a GUI that shows the
 *value of the counter as the game is being played.
 *
 * @author Courtney Cox
 * @version November 2017
 */
class MancComputerPlayer2 extends GameComputerPlayer implements Tickable {

	/*
	 * instance variables
	 */

	// the most recent game state, as given to us by the CounterLocalGame
	private MancState recentState = null;
	private boolean turn;
	// If this player is running the GUI, the activity (null if the player is
	// not running a GUI).
	private Activity activityForGui = null;
	private MancalaAnimator animator;

	// If this player is running the GUI, the widget containing the counter's
	// value (otherwise, null);
	private TextView counterValueTextView = null;

	// If this player is running the GUI, the handler for the GUI thread (otherwise
	// null)
	private Handler guiHandler = null;
	boolean reset;

	/**
	 * constructor
	 *
	 * @param name
	 * 		the player's name
	 */
	MancComputerPlayer2(String name) {
		super(name);
		getTimer().setInterval(50);
		getTimer().start();
	}

	/**
	 * callback method--game's state has changed
	 *
	 * @param info
	 * 		the information (presumably containing the game's state)
	 */
	@Override
	protected void receiveInfo(GameInfo info) {

		Log.i("computer player", "receiving");

		// if there is game and is a MancGame
		if (game != null && info instanceof MancState) {
			// if we indeed have a Manc-state, update the GUI
			recentState = (MancState)info;
			reset = recentState.getReset();
			turn=false;
			if(recentState.getPlayer_Turn()==this.playerNum){
				turn=true;
			}
			if(activityForGui!=null) {
				if(recentState.getReset()){
					animator.reset=true;
				}
				animator.setState(recentState);
				animator.setMarbles(this.playerNum);
			}

		}
	}//receiveInfo


	/**
	 * Tells whether we support a GUI
	 *
	 * @return
	 * 		true because we support a GUI
	 */
	public boolean supportsGui() {
		return true;
	}//supportsGui

	/**
	 * callback method--our player has been chosen/rechosen to be the GUI,
	 * called from the GUI thread.
	 *
	 * @param a
	 * 		the activity under which we are running
	 */
	@Override
	public void setAsGui(GameMainActivity a) {

		// remember who our activity is
		this.activityForGui = a;

		// remember the handler for the GUI thread
		this.guiHandler = new Handler();

		// Load the layout resource for the our GUI's configuration
		activityForGui.setContentView(R.layout.manc_layout);

		AnimationSurface mySurface =
				(AnimationSurface) activityForGui.findViewById(R.id.animation_surface);
		animator = new MancalaAnimator();
		mySurface.setAnimator(animator);

		Display mdisp = activityForGui.getWindowManager().getDefaultDisplay();
		Point mdispSize = new Point();
		mdisp.getSize(mdispSize);
		float maxX = (float)mdispSize.x;
		float maxY = (float)mdispSize.y;
		animator.getBounds(maxX,maxY);

		if(recentState == null) {
			recentState = animator.setHoles();
			recentState = animator.setMarbles(this.playerNum);
		}
		else {

			recentState = animator.setMarbles(this.playerNum);
		}//setAsGui

	}//setAsGui
	/**
	 * Implements the "brains" of the smart computer player.
	 * checks for potential double moves and captures.
	 * add in calculating opponent caputures
	 */
	@Override
	protected void timerTicked() {
		super.timerTicked();
		//checks if there is a game

		if(recentState != null && turn && !reset){
			//saves the array with the number of marbles in the holes

			int marbles[][] = recentState.getMarble_Pos();

			MyPointF select= new MyPointF(0,0);
			boolean holeSelect = false;

			//Checks for holes with marbles that will end in the bank
			for(int c=5; c>=0;c--){
				int marb = marbles[this.playerNum][c];
				if(marb == 6-c && !holeSelect){
					select=new MyPointF(this.playerNum, c);
					holeSelect=true;
				}
			}

			//If there is no bank ending move, checks for a capture move
			if(!holeSelect){
				//starts at hole one since that is the first hole to pick that can still play on
				// our side
				for(int c=0; c<6;c++ ){
					int marb = marbles[this.playerNum][c];
					if(marb>0 && marb+c<6 && !holeSelect){
						int future = marbles[this.playerNum][marb+c];
						int op;

						if(this.playerNum==0){
							op=1;
						}else{
							op=0;
						}

						if(future==0 && marbles[op][5-marb]>0){
                            select = new MyPointF(this.playerNum, c);
							holeSelect = true;
						}

					}
				}

			}
			if(!holeSelect){ //selecting the hole with the most marbles on that side

				int pos=0;
				for(int i=0; i<5; i++){

					if(marbles[this.playerNum][pos]<marbles[this.playerNum][i+1]){

						pos=i+1;
					}
				}
				select= new MyPointF(this.playerNum,pos );
				holeSelect= true;
			}
			//if none of the above options, selects a random move.
			if(!holeSelect) {

				//the holes range:
				int max = 5;
				int min = 0;
				//boolean to see if
				Boolean repick = true;
				//initialize column number to be zero
				int randPosition = 0;

				while (repick) {

					randPosition = (int) (Math.random() * max - min) + min;
					//if hole isn't empty, break while loop
					if (marbles[this.playerNum][randPosition] != 0) {
						repick = false;
					}
				}
				//set selected hole to randomly chosen number
				select = new MyPointF(this.playerNum, randPosition);
			}
			// send the move-action to the game if it is the computer turn

			//add some thinking time
			sleep(4000);

			//game.sendAction(new MancMoveAction(this, select, this.playerNum));
			recentState.setSelected_Hole(select);
			game.sendAction(new MancMoveAction(this, recentState, this.playerNum));
			turn=false;



		}

	}//timerTicked
}
