import java.util.*;
import java.util.stream.*;

public class Ramps {
    // I was limited to eight feet max because of the vehicle I used to get the boards
    public static final double INITIAL_BOARD_LENGTH = 96.0;

    public static void main(String[] args) {
        // The required cuts as defined in the plans, plus epsilon to account for saw blade width and cut variance
        double[] cuts = {
                65.15, 65.15,
                41.57, 41.57,
                38.07, 38.07,
                34.57, 34.57,
                31.07, 31.07,
                27.57, 27.57,
                24.07, 24.07,
                11.57, 11.57,
                3.57, 3.57
        };

        List<Board> boards = new LinkedList<>();
        boards.add(new Board(1)); // Have to have at least one board (obviously)
        Board currentBoard, targetBoard;
        double smallestRemainder;
        int currentCut = 0;
        int numberOfCuts = cuts.length;

        while (currentCut < numberOfCuts) {
            // Start over at the first board each time so you can find the best fit
            currentBoard = boards.get(0);
            targetBoard = currentBoard;
            smallestRemainder = INITIAL_BOARD_LENGTH;
            while (true) {
                if (currentBoard.canFit(cuts[currentCut])) {
                    if (currentBoard.getRemainderIfCut(cuts[currentCut]) < smallestRemainder) {
                        smallestRemainder = currentBoard.getRemainderIfCut(cuts[currentCut]);
                        targetBoard = currentBoard;
                    }
                    if (currentBoard.hasNext()) {
                        currentBoard = currentBoard.getNext();
                    } else {
                        break;
                    }
                } else {
                    if (currentBoard.hasNext()) {
                        currentBoard = currentBoard.getNext();
                    } else {
                        Board newBoard = new Board(boards.size() + 1);
                        currentBoard.setNext(newBoard);
                        newBoard.setPrevious(currentBoard);
                        currentBoard = newBoard;
                        boards.add(newBoard);
                    }
                }
            }
            targetBoard.add(cuts[currentCut++]);
        }

        for (Board board : boards) {
            System.out.println(board);
        }
    }
}

class Board {
    private final List<Double> cuts;
    private final int boardNumber;

    private Board previous;
    private Board next;
    private double currentLength;

    public Board(final int boardNumber) {
        cuts = new ArrayList<>();
        this.boardNumber = boardNumber;
        currentLength = Ramps.INITIAL_BOARD_LENGTH;
    }

//    public int getBoardNumber() {
//        return boardNumber;
//    }

//    public double getCurrentLength() {
//        return currentLength;
//    }

    /**
     * Returns the remainder of the length of this board if the specified length is removed from it
     * @param length the length in inches of the section of wood to be removed from this board
     * @return the length in inches of the remainder of this board if the specified amount were removed
     */
    public double getRemainderIfCut(final double length) {
        return currentLength - length;
    }

    /**
     * Returns the current remainder of this board
     * @return the current
     */
    public double getCurrentRemainder() {
        return getRemainderIfCut(0);
    }

//    public Board getPrevious() {
//        return previous;
//    }

    public void setPrevious(final Board previous) {
        this.previous = previous;
    }

//    public boolean hasPrevious() {
//        return null != previous;
//    }

    public Board getNext() {
        return next;
    }

    public void setNext(final Board next) {
        this.next = next;
    }

    public boolean hasNext() {
        return null != next;
    }

    public boolean canFit(final double length) {
        return currentLength > length;
    }

    public void add(final double length) {
        cuts.add(length);
        currentLength -= length;
    }

//    public void remove(final double length) {
//        cuts.remove(length);
//        currentLength += length;
//    }

    public String toString() {
        return "Board #" + boardNumber +
                "; Cuts: " + cuts.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]")) +
                "; Total Length of Cuts: " + (Ramps.INITIAL_BOARD_LENGTH - currentLength) +
                "; Total Remainder: " + getCurrentRemainder();
    }
}