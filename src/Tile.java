import java.util.ArrayList;

public class Tile {

	private final int x;
	private final int y;
	private boolean revealed = false;
	private boolean marked = false;
	private boolean bomb = false;
	private int neighbouringBombs;
	private ArrayList<Tile> neighbours = new ArrayList<Tile>();


	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int countNeighbouringBombs(){
		int count = 0;
		for (Tile n : neighbours){
			if(n.isBomb()){
				count++;
			}
		}
		neighbouringBombs = count;
		return count;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getNeighbouringBombs() { return neighbouringBombs; }

	public void setNeighbouringBombs(int neighbouringBombs) { this.neighbouringBombs = neighbouringBombs; }

	public void addNeighbour(Tile n){ neighbours.add(n); }

	public ArrayList<Tile> getNeighbours() { return neighbours; }

	public void clicked() {	revealed = true; }

	public void rightClicked() { marked = !marked; }

	public boolean isMarked() { return marked; }

	public boolean isVisible() {return revealed; }

	public boolean isBomb() {return bomb; }

	public void setAsBomb() {bomb =true; }

	public boolean equals(Tile other){
		if(this.x == other.getX() && this.y == other.getY()){
			return true;
		}else {
			return false;
		}
	}

}
