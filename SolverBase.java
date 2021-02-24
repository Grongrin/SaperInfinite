import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class SolverBase {
    private Board board = null;
    private ArrayList<Tile> safeLeftClicks;
    private ArrayList<Tile> safeRightClicks;


    public SolverBase(Board b){
        board = b;
        safeLeftClicks = new ArrayList<>();
        safeRightClicks = new ArrayList<>();
    }

    public void leftClick(int x, int y){
        if(board == null){
            return;
        }

        board.mouseClicked(new MouseEvent(board, MouseEvent.MOUSE_CLICKED, 0, 0, x*board.getTileSize(), y*board.getTileSize(), 1, false, 1));
        return;
    }

    public void rightClick(int x, int y){
        if(board == null){
            return;
        }

        board.mouseClicked(new MouseEvent(board, MouseEvent.MOUSE_CLICKED, 0, 0, x*board.getTileSize(), y*board.getTileSize(), 1, false, 3));
        return;
    }

    public boolean trySafeMove(){
        if (safeLeftClicks.size() > 0){
            leftClick(safeLeftClicks.get(0).getX(), safeLeftClicks.get(0).getY());
            safeLeftClicks.remove(0);
            return true;
        }else if(safeRightClicks.size() > 0){
            rightClick(safeRightClicks.get(0).getX(), safeRightClicks.get(0).getY());
            safeRightClicks.remove(0);
            return true;
        }
        return false;
    }

    public boolean makeMove(){

        if (trySafeMove()){
            return true;
        }

        for (HashMap.Entry<Integer, HashMap<Integer, Tile>> entry : board.getTiles().entrySet()) {
            for (HashMap.Entry<Integer, Tile> innerEntry : entry.getValue().entrySet()) {
                Tile tile = innerEntry.getValue();
                if (tile.isVisible()) {

                    int markedBombs = 0;
                    int undiscovered = 0;
                    for (Tile n : tile.getNeighbours()) {
                        if (n.isMarked()){
                            markedBombs++;
                        }else if(!n.isVisible()){
                            undiscovered++;
                        }
                    }

                    if(tile.getNeighbouringBombs() == markedBombs){
                        for (Tile n : tile.getNeighbours()) {
                            if (!n.isVisible() && !n.isMarked()){
                                if (!safeLeftClicks.contains(n)) {
                                    safeLeftClicks.add(n);
                                }
                            }
                        }
                    }else if(tile.getNeighbouringBombs() - markedBombs == undiscovered){
                        for (Tile n : tile.getNeighbours()) {
                            if (!n.isVisible() && !n.isMarked()){
                                if(!safeRightClicks.contains(n)) {
                                    safeRightClicks.add(n);
                                }
                            }
                        }
                    }


                }
            }
        }

        if (trySafeMove()){
            return true;
        }

        System.out.println("No more moves from first two rules, looking for more options...");

        for (HashMap.Entry<Integer, HashMap<Integer, Tile>> entry : board.getTiles().entrySet()) {
            for (HashMap.Entry<Integer, Tile> innerEntry : entry.getValue().entrySet()) {
                Tile tile = innerEntry.getValue();
                if (tile.isVisible()) {

                    int markedBombs = 0;
                    int undiscovered = 0;
                    for (Tile n : tile.getNeighbours()) {
                        if (n.isMarked()){
                            markedBombs++;
                        }else if(!n.isVisible()){
                            undiscovered++;
                        }
                    }

                    if(tile.getNeighbouringBombs() - markedBombs == 1){
                        ArrayList<Tile> multiTile = new ArrayList<>();
                        ArrayList<Tile> toCheck = new ArrayList<>();
                        for (Tile n : tile.getNeighbours()) {
                            if (!n.isVisible() && !n.isMarked()){
                                multiTile.add(n);
                            }
                        }

                        int x = tile.getX();
                        int y = tile.getY();
                        for(int x_ = x-2; x_ < x+3; x_++) {
                            for (int y_ = y - 2; y_ < y + 3; y_++) {
                                if (board.getTiles().containsKey(x_)) {
                                    if (board.getTiles().get(x_).containsKey(y_)) {
                                        Tile t = board.getTiles().get(x_).get(y_);
                                        if (t.isVisible() && !t.equals(tile) && !multiTile.contains(t)){
                                            boolean flag = false;
                                            for (Tile n : t.getNeighbours()) {
                                                if (multiTile.contains(n)){
                                                    if(flag){
                                                        toCheck.add(t);
                                                        break;
                                                    }
                                                    flag = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        for(Tile t : toCheck){
                            markedBombs = 0;
                            undiscovered = 0;
                            int connected = 0;
                            for (Tile n : t.getNeighbours()) {
                                if (n.isMarked()){
                                    markedBombs++;
                                }else if(!n.isVisible()){
                                    undiscovered++;
                                    if(multiTile.contains(n)){
                                        connected++;
                                    }
                                }
                            }
                            if(t.getNeighbouringBombs() - markedBombs == 2 && undiscovered - connected == 1){
                                for (Tile n : t.getNeighbours()) {
                                    if(!n.isVisible() && !n.isMarked() && !multiTile.contains(n)){
                                        if(!safeRightClicks.contains(n)) {
                                            System.out.println("Found a safe move (bomb) with the third rule! At point: ("+n.getX()+", "+n.getY()+").");
                                            safeRightClicks.add(n);
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        if (trySafeMove()){
            return true;
        }

        return false;

    }

    public void solve(){
        for(int i = 0; i < 100; i++){
            if(!makeMove()){
                break;
            }
        }
    }


}
