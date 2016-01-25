package CommonPackageServer;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;


@Serializable
public class MessageFromClient extends AbstractMessage {

	private int row;
	private int col;
	private boolean resign;

        public MessageFromClient() {}    // empty constructor required by jme3
        
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
