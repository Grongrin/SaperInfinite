import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, HashMap<Integer, Tile>> tiles = new HashMap<>();
	private int size = 14;
	private boolean firstClick = true;
	private double isBombChance = 0.2;
	private double multipleBombFactor = 0.9;
	private Random rnd = new Random();
	private BufferedImage red_flag_img = null;
	private int rightEdge = 1, downEdge = 1, leftEdge = 0, upEdge = 0;

	public Board() {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);

		try {
			red_flag_img = ImageIO.read(new File("resources/red_flag.png"));
		} catch (IOException e) {
		}
	}

	public HashMap<Integer, HashMap<Integer, Tile>> getTiles() {
		return tiles;
	}

	public boolean madeFirstMove() {
		return !firstClick;
	}

	public int getTileSize(){
		return size;
	}

	// clearing board
	public void clear() {
		tiles.clear();
		firstClick = true;
		rightEdge = 1;
		downEdge = 1;
		leftEdge = 0;
		upEdge = 0;
		setPreferredSize(new Dimension((rightEdge-leftEdge+5) * size,(downEdge-upEdge+5) * size));
		revalidate();
		this.repaint();

	}

	private void initialize(int length, int height) {

	}

	//paint background and separators between cells
	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(new Color(0xdddddd));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	private int getXOffset(){
		return (- leftEdge+2)*size;
	}

	private int getYOffset(){
		return (- upEdge+2)*size;
	}

	// draws the background netting
	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

		int xOffset = getXOffset();
		int yOffset = getYOffset();
		g.setFont(new Font("default", Font.BOLD, 16));
		for (HashMap.Entry<Integer, HashMap<Integer, Tile>> entry : tiles.entrySet()) {
			for (HashMap.Entry<Integer, Tile> innerEntry : entry.getValue().entrySet()) {
				Tile tile = innerEntry.getValue();
				if (tile.isVisible()) {
					if (tile.isBomb()) {
						g.setColor(new Color(0xff0000));
						g.fillRect((tile.getX() * size) + 1+xOffset, (tile.getY() * size) + 1+yOffset, (size - 1), (size - 1));
					}else if(tile.getNeighbouringBombs() > 0) {
						g.setColor(new Color(0x0000ff));
						g.fillRect((tile.getX() * size) + 1+xOffset, (tile.getY() * size) + 1+yOffset, (size - 1), (size - 1));
						g.setColor(new Color(0xffffff));
						g.drawString(Integer.toString(tile.getNeighbouringBombs()), (tile.getX() * size) + 3+xOffset, (tile.getY() * size) + size+yOffset);
					}else {
						g.setColor(new Color(0xffffff));
						g.fillRect((tile.getX() * size) + 1+xOffset, (tile.getY() * size) + 1+yOffset, (size - 1), (size - 1));
					}
				}else if (tile.isMarked()){
					g.drawImage(red_flag_img, (tile.getX() * size) + 1+xOffset,(tile.getY() * size) + 1+yOffset, (size - 1), (size - 1), null);
				}

			}
		}


	}

	public int countNeighbouringBombs(int x, int y){
		int bombs = 0;
		for (int x_ = x - 1; x_ < x + 2; x_++) {
			for (int y_ = y - 1; y_ < y + 2; y_++) {
				if (tiles.containsKey(x_)) {
					if (tiles.get(x_).containsKey(y_)) {
						if(tiles.get(x_).get(y_).isBomb())
							bombs++;
					}
				}
			}
		}
		return bombs;
	}

	public void determineIfBomb(int x, int y) {

		if(rnd.nextDouble() < (isBombChance * Math.pow(multipleBombFactor, countNeighbouringBombs(x, y)))){
			tiles.get(x).get(y).setAsBomb();
		}
	}

	public void click(int x, int y) {
		if (tiles.containsKey(x)) {
			if (tiles.get(x).containsKey(y)) {
				Tile tile = tiles.get(x).get(y);
				if (tile.isVisible()) {
					return;
				} else {
					if (tile.isMarked()){
						return;
					}
					tiles.get(x).get(y).clicked();
					if(rightEdge < x+1){
						rightEdge = x+1;
					}
					if(downEdge < y+1){
						downEdge = y+1;
					}
					if(leftEdge > x-1){
						leftEdge = x-1;
					}
					if(upEdge > y-1){
						upEdge = y-1;
					}
					for(int x_ = x-1; x_ < x+2; x_++){
						for(int y_ = y-1; y_ < y+2; y_++){
							if(x_ != x || y_ != y) {
								if (tiles.containsKey(x_)) {
									if (tiles.get(x_).containsKey(y_)) {
									} else {
										Tile newTile = new Tile(x_, y_);
										tiles.get(x_).put(y_, newTile);
										determineIfBomb(x_, y_);
									}
								} else {
									Tile newTile = new Tile(x_, y_);
									tiles.put(x_, new HashMap<Integer, Tile>());
									tiles.get(x_).put(y_, newTile);
									determineIfBomb(x_, y_);
								}

								tile.addNeighbour(tiles.get(x_).get(y_));
							}
						}
					}
//					int nBombs = countNeighbouringBombs(x, y);
//					tiles.get(x).get(y).setNeighbouringBombs(nBombs);
					if(tile.countNeighbouringBombs() == 0){
						for(int x_ = x-1; x_ < x+2; x_++) {
							for (int y_ = y - 1; y_ < y + 2; y_++) {
								click(x_, y_);
							}
						}
					}
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		int x;
		int y;
		if(e.getWhen() == 0){
			x = e.getX() / size;
			y = e.getY() / size;
		}else {
//			int xOffset = leftEdge*size;
//			int yOffset = upEdge*size;
			x = (int)Math.floor((e.getX()-getXOffset()) / (double)size);
			y = (int)Math.floor((e.getY()-getYOffset()) / (double)size);
			System.out.println("Mouse clicked at: ("+x + ", "+y + "); Button pressed: " + e.getButton());
		}

		if(firstClick && e.getButton() == 1){
			rightEdge = x + 1;
			downEdge = y + 1;
			System.out.println("Its the first click!");
			for(int x_ = x-1; x_ < x+2; x_++) {
				tiles.put(x_, new HashMap<Integer, Tile>());
				for (int y_ = y - 1; y_ < y + 2; y_++) {
					Tile newTile = new Tile(x_, y_);
					tiles.get(x_).put(y_, newTile);
				}
			}
			System.out.println("Initialized first 9 cells");

			for(int x_ = x-1; x_ < x+2; x_++) {
				for (int y_ = y - 1; y_ < y + 2; y_++) {
					click(x_, y_);
				}
			}
			System.out.println("Clicked the first cells");
			setPreferredSize(new Dimension((rightEdge-leftEdge+5) * size,(downEdge-upEdge+5) * size));
			revalidate();
			this.repaint();
			System.out.println("Repainted");


		}else {
			if(tiles.containsKey(x)){
				if(tiles.get(x).containsKey(y)){
					if(tiles.get(x).get(y).isVisible()){
						if(e.getButton() == 3) {
							for (int x_ = x - 1; x_ < x + 2; x_++) {
								for (int y_ = y - 1; y_ < y + 2; y_++) {
									click(x_, y_);
								}
							}
						}
					}else{
						if(e.getButton() == 1) {
							click(x, y);
						}else if(e.getButton() == 3){
							tiles.get(x).get(y).rightClicked();
							if(e.getWhen() == 0 && !tiles.get(x).get(y).isBomb()){
								System.out.println("Solver made a mistake! - Marked ("+x+", "+y+") as a bomb, while it is not!");
							}
						}
					}
					setPreferredSize(new Dimension((rightEdge-leftEdge+5) * size,(downEdge-upEdge+5) * size));
					revalidate();
					this.repaint();
				}
			}

			return;
		}
		firstClick = false;
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
		setPreferredSize(new Dimension((rightEdge-leftEdge+5) * size,(downEdge-upEdge+5) * size));
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
