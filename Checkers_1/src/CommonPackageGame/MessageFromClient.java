package CommonPackageGame;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;


@Serializable
public class MessageFromClient extends AbstractMessage {

	/**
	 * Message which client sends to server
	 */
//	private static final long serialVersionUID = -8365257093223296190L;

	private int row;
	private int col;
	private boolean resign;

        public MessageFromClient() {}    // empty constructor
        
	public boolean isResign() {
		return resign;
	}

	public void setResign(boolean resign) {
		this.resign = resign;
	}

	public int getChosenRow() {
		return row;
	}

	public void setChosenRow(int row) {
		this.row = row;
	}

	public int getChosenCol() {
		return col;
	}

	public void setChosenCol(int col) {
		this.col = col;
	}

}
