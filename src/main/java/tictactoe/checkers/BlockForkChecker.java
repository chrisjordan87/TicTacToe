package tictactoe.checkers;

import tictactoe.data.Board;
import tictactoe.data.NextMoveResult;
import tictactoe.data.Position;
import tictactoe.data.Seed;
import tictactoe.builders.BoardBuilder;
import tictactoe.utils.SeedUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockForkChecker implements Checker
{
    @Override
    public NextMoveResult check(Board board, Seed seed)
    {
        NextMoveResult nextMoveResult = new ForkChecker().check(board, SeedUtils.getOtherPlayer(seed));

        List<Position> possibleForkingPositions = nextMoveResult.getNextMoves();
        if (possibleForkingPositions.size() == 1)
        {
            return new NextMoveResult(possibleForkingPositions.get(0));
        }
        else if (possibleForkingPositions.size() > 1)
        {
            return forceOtherPlayerToGoInNonForkingPosition(possibleForkingPositions, board);
        }
        else
        {
            return NextMoveResult.indeterminateResult();
        }
    }

    private NextMoveResult forceOtherPlayerToGoInNonForkingPosition(List<Position> forkingPositions, Board board)
    {
        List<Position> potentialNewPositions = new ArrayList<>();

        for (Position emptyPosition : board.getEmptyPositions())
        {
            Board boardWithAdditionalMove = new BoardBuilder()
                    .withBoard(board)
                    .withMove(emptyPosition, Seed.COMPUTER)
                    .build();

            if (willForceOpponentToNonForkingPosition(forkingPositions, emptyPosition, boardWithAdditionalMove))
            {
                potentialNewPositions.add(emptyPosition);
            }

        }
        return new NextMoveResult(potentialNewPositions);
    }

    private boolean willForceOpponentToNonForkingPosition(List<Position> forkingPositions, Position emptyPosition, Board boardWithAdditionalMove)
    {
        return boardWithAdditionalMove.canSeedWin(Seed.COMPUTER).hasBeenDetermined()
                && !forkingPositions.contains(emptyPosition);
    }
}