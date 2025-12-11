package main;

import java.util.ArrayList;
import java.util.List;

public class MoveSnapshot {
    public final Piece movedPiece;
    public final Position from;
    public final Position to;
    public final Piece capturedPiece;
    public final Position capturedPos;

    public final Position enPassantTargetBefore;

    // Состояние hasMoved до хода для ВСЕХ затронутых фигур
    public final boolean movedPieceHasMovedBefore;
    public final boolean capturedPieceHasMovedBefore;
    public final boolean whiteKingHasMovedBefore;
    public final boolean blackKingHasMovedBefore;
    public final boolean whiteRookKingSideHasMovedBefore;
    public final boolean whiteRookQueenSideHasMovedBefore;
    public final boolean blackRookKingSideHasMovedBefore;
    public final boolean blackRookQueenSideHasMovedBefore;

    // Для отслеживания дополнительных фигур при рокировке
    public final Piece castlingRook;
    public final boolean castlingRookHasMovedBefore;

    public final boolean isCastling;
    public final boolean isEnPassant;
    public final boolean isPromotion;

    public MoveSnapshot(Piece movedPiece, Position from, Position to,
                        Piece capturedPiece, Position capturedPos,
                        Position enPassantTargetBefore,
                        boolean movedPieceHasMovedBefore,
                        boolean capturedPieceHasMovedBefore,
                        boolean whiteKingHasMovedBefore,
                        boolean blackKingHasMovedBefore,
                        boolean whiteRookKingSideHasMovedBefore,
                        boolean whiteRookQueenSideHasMovedBefore,
                        boolean blackRookKingSideHasMovedBefore,
                        boolean blackRookQueenSideHasMovedBefore,
                        Piece castlingRook,
                        boolean castlingRookHasMovedBefore,
                        boolean isCastling, boolean isEnPassant, boolean isPromotion) {
        this.movedPiece = movedPiece;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
        this.capturedPos = capturedPos;
        this.enPassantTargetBefore = enPassantTargetBefore;
        this.movedPieceHasMovedBefore = movedPieceHasMovedBefore;
        this.capturedPieceHasMovedBefore = capturedPieceHasMovedBefore;
        this.whiteKingHasMovedBefore = whiteKingHasMovedBefore;
        this.blackKingHasMovedBefore = blackKingHasMovedBefore;
        this.whiteRookKingSideHasMovedBefore = whiteRookKingSideHasMovedBefore;
        this.whiteRookQueenSideHasMovedBefore = whiteRookQueenSideHasMovedBefore;
        this.blackRookKingSideHasMovedBefore = blackRookKingSideHasMovedBefore;
        this.blackRookQueenSideHasMovedBefore = blackRookQueenSideHasMovedBefore;
        this.castlingRook = castlingRook;
        this.castlingRookHasMovedBefore = castlingRookHasMovedBefore;
        this.isCastling = isCastling;
        this.isEnPassant = isEnPassant;
        this.isPromotion = isPromotion;
    }
}