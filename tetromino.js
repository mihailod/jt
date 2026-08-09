/*
 * JT -- JavaTetris :: tetromino geometry
 *
 * A faithful transliteration of Tetromino.java (Mihailo Despotovic, 2002).
 * Every collision offset, spawn adjustment and orientation quirk is carried
 * over literally, including the URDL -> DRUL orientation remap. The point is
 * that it plays exactly like the applet did, so the hand-tuned numbers are
 * kept even where a generic matrix rotation would be shorter.
 */
window.JT = window.JT || {};

(function (JT) {
  'use strict';

  const CUBE = 0, SNAKE = 1, ZIG = 2, ZAG = 3, T = 4, L = 5, L_INV = 6;

  // orientations
  const UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

  const E = 0; // empty -- was Color.black

  // Cell values are shape + 1, so 0 stays "empty". AWT colours, verbatim.
  const COLORS = [
    '#0000ff', // CUBE   Color.blue
    '#ff0000', // SNAKE  Color.red
    '#00ffff', // ZIG    Color.cyan
    '#00ff00', // ZAG    Color.green
    '#ffff00', // T      Color.yellow
    '#ff00ff', // L      Color.magenta
    '#ffffff'  // L_INV  Color.white
  ];

  // The Java version indexed JT.table directly and relied on the x>0 / y<17
  // guards to stay in bounds. Out of range reads go through cell(), which
  // reports "blocked" instead of throwing.
  const cell = (x, y) => JT.cell(x, y);
  const set = (x, y, v) => JT.set(x, y, v);

  let shape = -1;
  let nextShape = Math.floor(Math.random() * 7);
  let orientation = -1;

  /** URDL -> DRUL */
  function convertOrientation() {
    switch (orientation) {
      case LEFT: return DOWN;
      case UP: return RIGHT;
      case RIGHT: return UP;
      case DOWN: return LEFT;
      default: return -1; // should never happen
    }
  }

  /** The Tetromino() constructor: takes the next shape and resets the position. */
  function spawn() {
    JT.p.x = 4; JT.p.y = -1;
    orientation = LEFT;
    shape = nextShape;
    nextShape = Math.floor(Math.random() * 7);

    // start position adjustments
    if (shape === SNAKE) JT.p.x++;
    if (shape === SNAKE || shape === CUBE) JT.p.y++;
  }

  function canMoveLeft(x, y) {
    switch (shape) {
      case CUBE:
        return x > 0 && cell(x - 1, y) === E && cell(x - 1, y + 1) === E;

      case SNAKE:
        if (orientation === UP || orientation === DOWN) {
          return x > 0 &&
            cell(x - 1, y - 1) === E && cell(x - 1, y) === E &&
            cell(x - 1, y + 1) === E && cell(x - 1, y + 2) === E;
        }
        return x > 2 && cell(x - 3, y) === E;

      case ZIG:
        if (orientation === UP || orientation === DOWN) {
          return x > 0 &&
            cell(x - 1, y + 1) === E && cell(x - 1, y + 2) === E &&
            cell(x, y) === E;
        }
        return x > 0 && cell(x - 1, y + 1) === E && cell(x, y + 2) === E;

      case ZAG:
        if (orientation === UP || orientation === DOWN) {
          return x > -1 &&
            cell(x, y) === E && cell(x, y + 1) === E &&
            cell(x + 1, y + 2) === E;
        }
        return x > 0 && cell(x - 1, y + 2) === E && cell(x, y + 1) === E;

      case T:
        switch (convertOrientation()) {
          case DOWN:
            return x > 0 && cell(x - 1, y + 1) === E && cell(x, y + 2) === E;
          case RIGHT:
            return x > -1 &&
              cell(x, y) === E && cell(x, y + 1) === E && cell(x, y + 2) === E;
          case UP:
            return x > 0 && cell(x, y) === E && cell(x - 1, y + 1) === E;
          case LEFT:
            return x > 0 &&
              cell(x, y) === E && cell(x - 1, y + 1) === E && cell(x, y + 2) === E;
        }
        return false;

      case L:
        switch (convertOrientation()) {
          case DOWN:
            return x > 0 && cell(x - 1, y + 1) === E && cell(x - 1, y + 2) === E;
          case RIGHT:
            return x > -1 &&
              cell(x, y) === E && cell(x, y + 1) === E && cell(x, y + 2) === E;
          case UP:
            return x > 0 && cell(x + 1, y) === E && cell(x - 1, y + 1) === E;
          case LEFT:
            return x > 0 &&
              cell(x - 1, y) === E && cell(x, y + 1) === E && cell(x, y + 2) === E;
        }
        return false;

      case L_INV:
        switch (convertOrientation()) {
          case DOWN:
            return x > 0 && cell(x - 1, y + 1) === E && cell(x + 1, y + 2) === E;
          case RIGHT:
            return x > -1 &&
              cell(x, y) === E && cell(x, y + 1) === E && cell(x, y + 2) === E;
          case UP:
            return x > 0 && cell(x - 1, y) === E && cell(x - 1, y + 1) === E;
          case LEFT:
            return x > 0 &&
              cell(x, y) === E && cell(x, y + 1) === E && cell(x - 1, y + 2) === E;
        }
        return false;
    }
    return false;
  }

  function canMoveRight(x, y) {
    switch (shape) {
      case CUBE:
        return x < 8 && cell(x + 2, y) === E && cell(x + 2, y + 1) === E;

      case SNAKE:
        if (orientation === UP || orientation === DOWN) {
          return x < 9 &&
            cell(x + 1, y - 1) === E && cell(x + 1, y) === E &&
            cell(x + 1, y + 1) === E && cell(x + 1, y + 2) === E;
        }
        return x < 8 && cell(x + 2, y) === E;

      case ZIG:
        if (orientation === UP || orientation === DOWN) {
          return x < 8 &&
            cell(x + 2, y) === E && cell(x + 2, y + 1) === E &&
            cell(x + 1, y + 2) === E;
        }
        return x < 7 && cell(x + 2, y + 1) === E && cell(x + 3, y + 2) === E;

      case ZAG:
        if (orientation === UP || orientation === DOWN) {
          return x < 7 &&
            cell(x + 2, y) === E && cell(x + 3, y + 1) === E &&
            cell(x + 3, y + 2) === E;
        }
        return x < 7 && cell(x + 3, y + 1) === E && cell(x + 2, y + 2) === E;

      case T:
        switch (convertOrientation()) {
          case DOWN:
            return x < 7 && cell(x + 3, y + 1) === E && cell(x + 2, y + 2) === E;
          case RIGHT:
            return x < 7 &&
              cell(x + 2, y) === E && cell(x + 3, y + 1) === E &&
              cell(x + 2, y + 2) === E;
          case UP:
            return x < 7 && cell(x + 2, y) === E && cell(x + 3, y + 1) === E;
          case LEFT:
            return x < 8 &&
              cell(x + 2, y) === E && cell(x + 2, y + 1) === E &&
              cell(x + 2, y + 2) === E;
        }
        return false;

      case L:
        switch (convertOrientation()) {
          case DOWN:
            return x < 7 && cell(x + 3, y + 1) === E && cell(x + 1, y + 2) === E;
          case RIGHT:
            return x < 7 &&
              cell(x + 2, y) === E && cell(x + 2, y + 1) === E &&
              cell(x + 3, y + 2) === E;
          case UP:
            return x < 7 && cell(x + 3, y) === E && cell(x + 3, y + 1) === E;
          case LEFT:
            return x < 8 &&
              cell(x + 2, y) === E && cell(x + 2, y + 1) === E &&
              cell(x + 2, y + 2) === E;
        }
        return false;

      case L_INV:
        switch (convertOrientation()) {
          case DOWN:
            return x < 7 && cell(x + 3, y + 1) === E && cell(x + 3, y + 2) === E;
          case RIGHT:
            return x < 7 &&
              cell(x + 3, y) === E && cell(x + 2, y + 1) === E &&
              cell(x + 2, y + 2) === E;
          case UP:
            return x < 7 && cell(x + 1, y) === E && cell(x + 3, y + 1) === E;
          case LEFT:
            return x < 8 &&
              cell(x + 2, y) === E && cell(x + 2, y + 1) === E &&
              cell(x + 2, y + 2) === E;
        }
        return false;
    }
    return false;
  }

  function canMoveDown(x, y) {
    switch (shape) {
      case CUBE:
        return y < 18 && cell(x, y + 2) === E && cell(x + 1, y + 2) === E;

      case SNAKE:
        if (orientation === UP || orientation === DOWN) {
          return y < 17 && cell(x, y + 3) === E;
        }
        return y < 19 &&
          cell(x - 2, y + 1) === E && cell(x - 1, y + 1) === E &&
          cell(x, y + 1) === E && cell(x + 1, y + 1) === E;

      case ZIG:
        if (orientation === UP || orientation === DOWN) {
          return y < 17 && cell(x, y + 3) === E && cell(x + 1, y + 2) === E;
        }
        return y < 17 &&
          cell(x, y + 2) === E && cell(x + 1, y + 3) === E &&
          cell(x + 2, y + 3) === E;

      case ZAG:
        if (orientation === UP || orientation === DOWN) {
          return y < 17 && cell(x + 1, y + 2) === E && cell(x + 2, y + 3) === E;
        }
        return y < 17 &&
          cell(x, y + 3) === E && cell(x + 1, y + 3) === E &&
          cell(x + 2, y + 2) === E;

      case T:
        switch (convertOrientation()) {
          case DOWN:
            return y < 17 &&
              cell(x, y + 2) === E && cell(x + 1, y + 3) === E &&
              cell(x + 2, y + 2) === E;
          case RIGHT:
            return y < 17 && cell(x + 1, y + 3) === E && cell(x + 2, y + 2) === E;
          case UP:
            return y < 18 &&
              cell(x, y + 2) === E && cell(x + 1, y + 2) === E &&
              cell(x + 2, y + 2) === E;
          case LEFT:
            return y < 17 && cell(x, y + 2) === E && cell(x + 1, y + 3) === E;
        }
        return false;

      case L:
        switch (convertOrientation()) {
          case DOWN:
            return y < 17 &&
              cell(x, y + 3) === E && cell(x + 1, y + 2) === E &&
              cell(x + 2, y + 2) === E;
          case RIGHT:
            return y < 17 && cell(x + 1, y + 3) === E && cell(x + 2, y + 3) === E;
          case UP:
            return y < 18 &&
              cell(x, y + 2) === E && cell(x + 1, y + 2) === E &&
              cell(x + 2, y + 2) === E;
          case LEFT:
            return y < 17 && cell(x, y + 1) === E && cell(x + 1, y + 3) === E;
        }
        return false;

      case L_INV:
        switch (convertOrientation()) {
          case DOWN:
            return y < 17 &&
              cell(x, y + 2) === E && cell(x + 1, y + 2) === E &&
              cell(x + 2, y + 3) === E;
          case RIGHT:
            return y < 17 && cell(x + 1, y + 3) === E && cell(x + 2, y + 1) === E;
          case UP:
            return y < 18 &&
              cell(x, y + 2) === E && cell(x + 1, y + 2) === E &&
              cell(x + 2, y + 2) === E;
          case LEFT:
            return y < 17 && cell(x, y + 3) === E && cell(x + 1, y + 3) === E;
        }
        return false;

      default:
        return true;
    }
  }

  function dropPosition(x, y) {
    let res;
    switch (shape) {
      case CUBE:
        res = 18;
        for (let i = y; i < 18; i++) {
          if (cell(x, i + 2) !== E || cell(x + 1, i + 2) !== E) { res = i; break; }
        }
        return res;

      case SNAKE:
        if (orientation === UP || orientation === DOWN) {
          res = 17;
          for (let i = y; i < 17; i++) {
            if (cell(x, i + 3) !== E) { res = i; break; }
          }
          return res;
        }
        res = 19;
        for (let i = y; i < 19; i++) {
          if (cell(x - 2, i + 1) !== E || cell(x - 1, i + 1) !== E ||
              cell(x, i + 1) !== E || cell(x + 1, i + 1) !== E) { res = i; break; }
        }
        return res;

      case ZIG:
        if (orientation === UP || orientation === DOWN) {
          res = 17;
          for (let i = y; i < 17; i++) {
            if (cell(x, i + 3) !== E || cell(x + 1, i + 2) !== E) { res = i; break; }
          }
          return res;
        }
        res = 17;
        for (let i = y; i < 17; i++) {
          if (cell(x, i + 2) !== E || cell(x + 1, i + 3) !== E ||
              cell(x + 2, i + 3) !== E) { res = i; break; }
        }
        return res;

      case ZAG:
        if (orientation === UP || orientation === DOWN) {
          res = 17;
          for (let i = y; i < 17; i++) {
            if (cell(x + 1, i + 2) !== E || cell(x + 2, i + 3) !== E) { res = i; break; }
          }
          return res;
        }
        res = 17;
        for (let i = y; i < 17; i++) {
          if (cell(x, i + 3) !== E || cell(x + 1, i + 3) !== E ||
              cell(x + 2, i + 2) !== E) { res = i; break; }
        }
        return res;

      case T:
        switch (convertOrientation()) {
          case DOWN:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x, i + 2) !== E || cell(x + 1, i + 3) !== E ||
                  cell(x + 2, i + 2) !== E) { res = i; break; }
            }
            return res;
          case RIGHT:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x + 1, i + 3) !== E || cell(x + 2, i + 2) !== E) { res = i; break; }
            }
            return res;
          case UP:
            res = 18;
            for (let i = y; i < 18; i++) {
              if (cell(x, i + 2) !== E || cell(x + 1, i + 2) !== E ||
                  cell(x + 2, i + 2) !== E) { res = i; break; }
            }
            return res;
          case LEFT:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x, i + 2) !== E || cell(x + 1, i + 3) !== E) { res = i; break; }
            }
            return res;
        }
        break;

      case L:
        switch (convertOrientation()) {
          case DOWN:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x, i + 3) !== E || cell(x + 1, i + 2) !== E ||
                  cell(x + 2, i + 2) !== E) { res = i; break; }
            }
            return res;
          case RIGHT:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x + 1, i + 3) !== E || cell(x + 2, i + 3) !== E) { res = i; break; }
            }
            return res;
          case UP:
            res = 18;
            for (let i = y; i < 18; i++) {
              if (cell(x, i + 2) !== E || cell(x + 1, i + 2) !== E ||
                  cell(x + 2, i + 2) !== E) { res = i; break; }
            }
            return res;
          case LEFT:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x, i + 1) !== E || cell(x + 1, i + 3) !== E) { res = i; break; }
            }
            return res;
        }
        break;

      case L_INV:
        switch (convertOrientation()) {
          case DOWN:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x, i + 2) !== E || cell(x + 1, i + 2) !== E ||
                  cell(x + 2, i + 3) !== E) { res = i; break; }
            }
            return res;
          case RIGHT:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x + 1, i + 3) !== E || cell(x + 2, i + 1) !== E) { res = i; break; }
            }
            return res;
          case UP:
            res = 18;
            for (let i = y; i < 18; i++) {
              if (cell(x, i + 2) !== E || cell(x + 1, i + 2) !== E ||
                  cell(x + 2, i + 2) !== E) { res = i; break; }
            }
            return res;
          case LEFT:
            res = 17;
            for (let i = y; i < 17; i++) {
              if (cell(x, i + 3) !== E || cell(x + 1, i + 3) !== E) { res = i; break; }
            }
            return res;
        }
        break;
    }
    throw new Error('What?'); // the original's IllegalArgumentException
  }

  function canRotate(x, y) {
    switch (shape) {
      case CUBE:
        return false;

      case SNAKE:
        if (orientation === UP || orientation === DOWN) {
          return x < 9 && x > 1 &&
            cell(x - 2, y) === E && cell(x - 1, y) === E && cell(x + 1, y) === E;
        }
        return y > 0 &&
          cell(x, y - 1) === E && cell(x, y + 1) === E && cell(x, y + 2) === E;

      case ZIG:
        if (orientation === UP || orientation === DOWN) {
          return x < 8 && cell(x + 1, y + 2) === E && cell(x + 2, y + 2) === E;
        }
        return y > -1 && cell(x, y + 2) === E && cell(x + 1, y) === E;

      case ZAG:
        if (orientation === UP || orientation === DOWN) {
          return x < 8 && x > -1 && cell(x, y + 2) === E && cell(x + 1, y + 2) === E;
        }
        return y > -1 && cell(x + 1, y) === E && cell(x + 2, y + 2) === E;

      case T:
        switch (convertOrientation()) {
          case DOWN: return y > -1 && cell(x + 1, y) === E;
          case RIGHT: return x > -1 && cell(x, y + 1) === E;
          case UP: return cell(x + 1, y + 2) === E;
          case LEFT: return x < 8 && cell(x + 2, y + 1) === E;
        }
        return false;

      case L:
        switch (convertOrientation()) {
          case DOWN:
            return y > -1 && cell(x + 1, y) === E && cell(x + 1, y + 2) === E &&
              cell(x + 2, y + 2) === E;
          case RIGHT:
            return y > -1 && x > -1 && cell(x + 2, y) === E &&
              cell(x + 2, y + 1) === E && cell(x, y + 1) === E;
          case UP:
            return y < 19 && y > -1 && cell(x, y) === E && cell(x + 1, y) === E &&
              cell(x + 1, y + 2) === E;
          case LEFT:
            return x < 8 && y > -1 && cell(x + 2, y + 1) === E &&
              cell(x, y + 1) === E && cell(x, y + 2) === E;
        }
        return false;

      case L_INV:
        switch (convertOrientation()) {
          case DOWN:
            return y > -1 && cell(x + 1, y) === E && cell(x + 2, y) === E &&
              cell(x + 1, y + 2) === E;
          case RIGHT:
            return y > -1 && x > -1 && cell(x, y) === E && cell(x, y + 1) === E &&
              cell(x + 2, y + 1) === E;
          case UP:
            return y < 19 && y > -1 && cell(x + 1, y) === E && cell(x, y + 2) === E &&
              cell(x + 1, y + 2) === E;
          case LEFT:
            return x < 8 && cell(x, y + 1) === E && cell(x + 2, y + 1) === E &&
              cell(x + 2, y + 2) === E;
        }
        return false;
    }
    return false;
  }

  function rotate() {
    if (shape === CUBE) return;
    orientation++; orientation %= 4;
  }

  /** Paints the piece into the board array, or erases it back to empty. */
  function draw(x, y, erase) {
    const c = erase ? E : shape + 1;

    switch (shape) {
      case CUBE:
        set(x, y, c); set(x + 1, y, c);
        set(x, y + 1, c); set(x + 1, y + 1, c);
        break;

      case SNAKE:
        if (orientation === UP || orientation === DOWN) {
          set(x, y - 1, c); set(x, y, c);
          set(x, y + 1, c); set(x, y + 2, c);
        } else {
          set(x - 2, y, c); set(x - 1, y, c);
          set(x, y, c); set(x + 1, y, c);
        }
        break;

      case ZIG:
        if (orientation === UP || orientation === DOWN) {
          set(x + 1, y, c); set(x + 1, y + 1, c);
          set(x, y + 1, c); set(x, y + 2, c);
        } else {
          set(x, y + 1, c); set(x + 1, y + 1, c);
          set(x + 1, y + 2, c); set(x + 2, y + 2, c);
        }
        break;

      case ZAG:
        if (orientation === UP || orientation === DOWN) {
          set(x + 1, y, c); set(x + 1, y + 1, c);
          set(x + 2, y + 1, c); set(x + 2, y + 2, c);
        } else {
          set(x + 1, y + 1, c); set(x + 2, y + 1, c);
          set(x, y + 2, c); set(x + 1, y + 2, c);
        }
        break;

      case T:
        switch (convertOrientation()) {
          case DOWN:
            set(x, y + 1, c); set(x + 1, y + 1, c);
            set(x + 2, y + 1, c); set(x + 1, y + 2, c);
            break;
          case RIGHT:
            set(x + 1, y, c); set(x + 1, y + 1, c);
            set(x + 2, y + 1, c); set(x + 1, y + 2, c);
            break;
          case UP:
            set(x, y + 1, c); set(x + 1, y + 1, c);
            set(x + 2, y + 1, c); set(x + 1, y, c);
            break;
          case LEFT:
            set(x, y + 1, c); set(x + 1, y + 1, c);
            set(x + 1, y, c); set(x + 1, y + 2, c);
            break;
        }
        break;

      case L:
        switch (convertOrientation()) {
          case DOWN:
            set(x, y + 1, c); set(x + 1, y + 1, c);
            set(x + 2, y + 1, c); set(x, y + 2, c);
            break;
          case RIGHT:
            set(x + 1, y, c); set(x + 1, y + 1, c);
            set(x + 1, y + 2, c); set(x + 2, y + 2, c);
            break;
          case UP:
            set(x + 2, y, c); set(x, y + 1, c);
            set(x + 1, y + 1, c); set(x + 2, y + 1, c);
            break;
          case LEFT:
            set(x, y, c); set(x + 1, y, c);
            set(x + 1, y + 1, c); set(x + 1, y + 2, c);
            break;
        }
        break;

      case L_INV:
        switch (convertOrientation()) {
          case DOWN:
            set(x, y + 1, c); set(x + 1, y + 1, c);
            set(x + 2, y + 1, c); set(x + 2, y + 2, c);
            break;
          case RIGHT:
            set(x + 1, y, c); set(x + 2, y, c);
            set(x + 1, y + 1, c); set(x + 1, y + 2, c);
            break;
          case UP:
            set(x, y, c); set(x, y + 1, c);
            set(x + 1, y + 1, c); set(x + 2, y + 1, c);
            break;
          case LEFT:
            set(x + 1, y, c); set(x + 1, y + 1, c);
            set(x + 1, y + 2, c); set(x, y + 2, c);
            break;
        }
        break;
    }
  }

  /** The preview in the left sidebar. */
  function drawNext(ctx) {
    const nx = JT.NEXT_X, ny = JT.NEXT_Y;
    ctx.fillStyle = COLORS[nextShape];

    switch (nextShape) {
      case SNAKE:
        ctx.fillRect(nx, ny, 80, 20);
        break;
      case CUBE:
        ctx.fillRect(nx + 20, ny, 40, 40);
        break;
      case T:
        ctx.fillRect(nx + 20, ny, 60, 20);
        ctx.fillRect(nx + 40, ny + 20, 20, 20);
        break;
      case ZIG:
        ctx.fillRect(nx + 20, ny, 40, 20);
        ctx.fillRect(nx + 40, ny + 20, 40, 20);
        break;
      case ZAG:
        ctx.fillRect(nx + 40, ny, 40, 20);
        ctx.fillRect(nx + 20, ny + 20, 40, 20);
        break;
      case L:
        ctx.fillRect(nx + 20, ny, 60, 20);
        ctx.fillRect(nx + 20, ny + 20, 20, 20);
        break;
      case L_INV:
        ctx.fillRect(nx + 20, ny, 60, 20);
        ctx.fillRect(nx + 60, ny + 20, 20, 20);
        break;
    }
  }

  JT.COLORS = COLORS;
  JT.EMPTY = E;
  JT.tet = {
    spawn, canMoveLeft, canMoveRight, canMoveDown,
    dropPosition, canRotate, rotate, draw, drawNext
  };
})(window.JT);
