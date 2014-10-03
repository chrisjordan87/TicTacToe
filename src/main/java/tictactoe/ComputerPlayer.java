package tictactoe;

import tictactoe.builders.BoardFactory;
import tictactoe.data.Board;
import tictactoe.data.GameProgress;
import tictactoe.data.Position;
import tictactoe.data.Mark;
import tictactoe.utils.MarkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputerPlayer implements Player {
    public static final int THIS_PLAYER_HAS_WON_SCORE = 10;
    public static final int OTHER_PLAYER_HAS_WON_SCORE = -10;
    public static final int DRAW_SCORE = 0;
    private Mark mark;

    public ComputerPlayer(Mark mark) {
        this.mark = mark;
    }

    @Override
    public GameProgress play(Board board) {
        updateBoardUsingMinimax(board);
        return null;
    }

    private void updateBoardUsingMinimax(Board board) {
        minimax(board, true, 0);
    }

    private int minimax(Board board, boolean thisPlayersTurn, int depth) {
        if (board.isGameOver()) {
            return calculateScore(board, depth);
        }
        depth++;

        List<Position> emptyPositions = board.getEmptyPositions();

        Map<Position, Integer> positionScores = new HashMap<>();

        for (Position emptyPosition : emptyPositions) {
            positionScores.put(emptyPosition,
                    minimax(BoardFactory.addMove(board, emptyPosition, determineMark(thisPlayersTurn)), !thisPlayersTurn, depth));
        }

     //   System.out.println("__________________");

//        if(emptyPositions.size()==8) {
//            System.out.println(thisPlayersTurn);
//            for (Position position : positionScores.keySet()) {
//                System.out.println("Score " + positionScores.get(position));
//                System.out.println(BoardFactory.addMove(board,position,Mark.X));
//            }
//
//        }


        if (thisPlayersTurn) {
            board.addMark(mark, getPositionWithMaximumScore(positionScores));
            int maximumScore = getMaximumScore(positionScores);
          //  System.out.println("max score: " + maximumScore);
            return maximumScore;
        } else {
            int minimumScore = getMinimumScore(positionScores);
          //  System.out.println("min score: " + minimumScore);
            return minimumScore;
        }
    }

    private int getMinimumScore(Map<Position, Integer> positionScores) {
        return positionScores.values().stream().min(Integer::compare).get();
    }

    private int getMaximumScore(Map<Position, Integer> positionScores) {
        return positionScores.values().stream().max(Integer::compare).get();
    }

    private Position getPositionWithMaximumScore(Map<Position, Integer> positionScores) {


        for (Position position : positionScores.keySet()) {
            if (positionScores.get(position) == getMaximumScore(positionScores)) {
                return position;
            }
        }

        throw new ComputerPlayerException("Computer has not determined best position from list of states");
    }

    private Mark determineMark(boolean thisPlayersTurn) {
        if (thisPlayersTurn) {
            return mark;
        } else {
            return MarkUtils.getOtherMark(mark);
        }
    }

    private int calculateScore(Board board, int depth) {
        if (board.hasSeedWon(mark)) {
            return THIS_PLAYER_HAS_WON_SCORE;
        } else if (board.hasSeedWon(MarkUtils.getOtherMark(mark))) {
            return OTHER_PLAYER_HAS_WON_SCORE;
        } else {
            return DRAW_SCORE;
        }
    }
}
