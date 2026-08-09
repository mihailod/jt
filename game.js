/*
 * JT -- JavaTetris :: game loop, rendering and input
 *
 * Transliterated from JT.java (Mihailo Despotovic, 2002), which was an
 * java.applet.Applet drawing into an offscreen AWT Image. Canvas 2D uses the
 * same immediate-mode fillRect/drawString model, so the pixel layout below is
 * unchanged: a 100px sidebar, then a 10x20 well of 20px cells.
 *
 * Deliberate departures from the 2002 code are marked "2002:".
 */
window.JT = window.JT || {};

(function (JT) {
  'use strict';

  const W = 301, H = 401;   // the old applet's size
  const L = 100;            // left edge of the well
  const CELL = 20;

  JT.NEXT_X = 10;
  JT.NEXT_Y = 350;

  const PLAY = 1, GAME_OVER = 2;
  const E = JT.EMPTY;

  const LIGHT_GRAY = '#c0c0c0';
  const YELLOW = '#ffff00';
  const RED = '#ff0000';
  const BLACK = '#000000';
  const WHITE = '#ffffff';
  const FONT = 'bold 13px "DejaVu Sans Mono", "Menlo", monospace';

  // ---- state ----------------------------------------------------------

  const table = [];
  for (let i = 0; i < 10; i++) table.push(new Array(20).fill(E));

  JT.p = { x: 0, y: -1 };

  let firstTime = true;
  let drawNextPiece = true;
  let score = 0;
  let highScore = 0;
  let highScoreAchieved = false;
  let gameState = GAME_OVER;
  let paused = false;
  let delay = 500;
  let level = 0;
  let startedAt = 0;
  let lastTime = '00:00:00';

  let canvas, ctx, scale = 1;

  // 2002: the board array was Color[10][20] and collisions compared against
  // Color.black by reference. Here cells are ints, and reads outside the well
  // report "blocked" rather than throwing the way Java's array access did.
  JT.cell = function (x, y) {
    if (x < 0 || x > 9 || y < 0 || y > 19) return -1;
    return table[x][y];
  };
  JT.set = function (x, y, v) {
    if (x < 0 || x > 9 || y < 0 || y > 19) return;
    table[x][y] = v;
  };

  const tet = () => JT.tet;

  // ---- sound ----------------------------------------------------------

  let audio = null;
  function ensureAudio() {
    if (!audio) {
      const AC = window.AudioContext || window.webkitAudioContext;
      if (AC) audio = new AC();
    }
    if (audio && audio.state === 'suspended') audio.resume();
  }

  // 2002: Toolkit.getDefaultToolkit().beep()
  function beep(freq, ms) {
    if (!audio) return;
    const osc = audio.createOscillator();
    const gain = audio.createGain();
    osc.type = 'square';
    osc.frequency.value = freq;
    gain.gain.setValueAtTime(0.06, audio.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.0001, audio.currentTime + ms / 1000);
    osc.connect(gain); gain.connect(audio.destination);
    osc.start();
    osc.stop(audio.currentTime + ms / 1000);
  }

  // ---- game -----------------------------------------------------------

  function startGame() {
    for (let i = 0; i < 10; i++)
      for (let j = 0; j < 20; j++)
        table[i][j] = E;

    highScoreAchieved = false;
    gameState = PLAY;
    paused = false;
    startedAt = performance.now();
    score = 0;
  }

  // 2002: checkAllLines() called checkLines() four times because the loop
  // skipped past a row it had just shifted ("stupid, but I am lazy"). One
  // correct pass replaces it. This also clears rows 0 and 1, which the old
  // `for(j=19; j>1; j--)` bound could never reach.
  function checkAllLines() {
    let cleared = 0;
    for (let j = 19; j >= 0; j--) {
      let full = true;
      for (let i = 0; i < 10; i++) {
        if (table[i][j] === E) { full = false; break; }
      }
      if (!full) continue;
      shiftLines(j);
      score++;
      cleared++;
      increaseLevel();
      j++; // re-check this row, it now holds whatever was above it
    }
    // 2002: beeped once per line; once per batch is easier on the ears.
    if (cleared > 0) beep(cleared >= 4 ? 1320 : 880, 70);
  }

  function shiftLines(line) {
    for (let i = 0; i < 10; i++)
      for (let j = line; j >= 0; j--)
        table[i][j] = (j === 0) ? E : table[i][j - 1];
  }

  function increaseLevel() {
    if (level === 9) return;
    if (gameState === PLAY) {
      const lev = Math.floor(score / 10);
      if (level < lev) {
        level = lev;
        delay = 500 - 50 * lev;
      }
    } else {
      level++;
      delay = 500 - 50 * level;
    }
  }

  function decreaseLevel() {
    if (level === 0) return;
    level--;
    delay = 500 - 50 * level;
  }

  function next() {
    tet().spawn();
    tet().draw(JT.p.x, JT.p.y, false);
    if (!tet().canMoveDown(JT.p.x, JT.p.y)) {
      firstTime = false;
      if (gameState !== GAME_OVER) beep(160, 320);
      gameState = GAME_OVER;
      if (score > highScore) {
        highScore = score;
        highScoreAchieved = true;
        saveHighScore();
      }
    }
  }

  /** One gravity tick -- the body of the old Mover thread's loop. */
  function step() {
    const p = JT.p;
    if (tet().canMoveDown(p.x, p.y)) {
      tet().draw(p.x, p.y, true);
      p.y++;
      tet().draw(p.x, p.y, false);
    } else {
      checkAllLines();
      next();
    }
  }

  // ---- input ----------------------------------------------------------

  function action(name) {
    ensureAudio();
    const p = JT.p;

    if (name === 'pause') {
      if (gameState === PLAY) { paused = !paused; return; }
      // otherwise fall through, so P counts as "any key" on the title screen
    }
    if (paused) { paused = false; return; }

    // 2002: any key but 5/6 starts a new game from the game over screen
    if (name !== 'slower' && name !== 'faster') {
      if (gameState === GAME_OVER) { startGame(); return; }
    }

    switch (name) {
      case 'left':
        if (tet().canMoveLeft(p.x, p.y)) {
          tet().draw(p.x, p.y, true);
          p.x--;
          tet().draw(p.x, p.y, false);
        }
        break;
      case 'right':
        if (tet().canMoveRight(p.x, p.y)) {
          tet().draw(p.x, p.y, true);
          p.x++;
          tet().draw(p.x, p.y, false);
        }
        break;
      case 'rotate':
        if (tet().canRotate(p.x, p.y)) {
          tet().draw(p.x, p.y, true);
          tet().rotate();
          tet().draw(p.x, p.y, false);
        }
        break;
      case 'drop':
        // 2002: t.stop() then a fresh Mover thread. Here: reset the fall clock.
        tet().draw(p.x, p.y, true);
        p.y = tet().dropPosition(p.x, p.y);
        tet().draw(p.x, p.y, false);
        beep(220, 40);
        checkAllLines();
        next();
        acc = 0;
        break;
      case 'toggleNext':
        drawNextPiece = !drawNextPiece;
        break;
      case 'faster':
        if (level < 9) { level++; delay = 500 - 50 * level; }
        break;
      case 'slower':
        if (gameState !== PLAY) decreaseLevel();
        break;
    }
  }

  const KEYS = {
    '7': 'left', 'ArrowLeft': 'left',
    '9': 'right', 'ArrowRight': 'right',
    '8': 'rotate', 'ArrowUp': 'rotate',
    '4': 'drop', ' ': 'drop', 'ArrowDown': 'drop',
    '1': 'toggleNext',
    '6': 'faster',
    '5': 'slower',
    'p': 'pause', 'P': 'pause', 'Escape': 'pause'
  };

  // Keys that shouldn't count as "any key": modifiers on their own, browser
  // function keys, and Tab, which has to keep working for keyboard navigation.
  const NOT_A_KEY = new Set([
    'Shift', 'Control', 'Alt', 'Meta', 'Tab', 'CapsLock',
    'NumLock', 'ScrollLock', 'ContextMenu', 'Dead', 'Unidentified'
  ]);

  function onKeyDown(e) {
    if (e.ctrlKey || e.metaKey || e.altKey) return;

    const name = KEYS[e.key];
    if (name) {
      e.preventDefault();
      action(name);
      return;
    }

    // 2002: keyPressed() fired for every key, so the title screen really did
    // mean "press any key". Anything unmapped still starts a game or unpauses.
    if (NOT_A_KEY.has(e.key)) return;
    if (e.key.length > 1 && /^F\d+$/.test(e.key)) return;
    if (gameState === GAME_OVER || paused) {
      e.preventDefault();
      action('any');
    }
  }

  // ---- persistence ----------------------------------------------------

  function loadHighScore() {
    try {
      const v = parseInt(window.localStorage.getItem('jt.highScore'), 10);
      if (!isNaN(v)) highScore = v;
    } catch (err) { /* private mode, no storage -- not worth caring about */ }
  }

  function saveHighScore() {
    try {
      window.localStorage.setItem('jt.highScore', String(highScore));
    } catch (err) { /* ditto */ }
  }

  // ---- rendering ------------------------------------------------------

  function centered(text, boxWidth) {
    return (boxWidth - ctx.measureText(text).width) / 2;
  }

  function drawFrame() {
    ctx.fillStyle = BLACK;
    ctx.fillRect(0, 0, 300, 401);
    ctx.fillStyle = RED;
    ctx.fillRect(L - 1, 0, 1, 401);      // left wall
    ctx.fillRect(L - 1, 400, 202, 1);    // floor
    ctx.fillRect(L + 200, 0, 1, 401);    // right wall
  }

  function drawTakenFields() {
    for (let i = 0; i < 10; i++) {
      for (let j = 0; j < 20; j++) {
        const v = table[i][j];
        if (v === E) continue; // the frame already painted it black
        ctx.fillStyle = JT.COLORS[v - 1];
        ctx.fillRect(L + i * CELL, j * CELL, CELL, CELL);
      }
    }
  }

  function drawTimer() {
    if (gameState === PLAY && !paused) {
      // 2002: new Date(now - startedAt - 16h).toString().substring(11,19),
      // which only read 00:00:00 on a machine at UTC-8.
      const t = Math.max(0, Math.floor((performance.now() - startedAt) / 1000));
      const hh = String(Math.floor(t / 3600)).padStart(2, '0');
      const mm = String(Math.floor(t / 60) % 60).padStart(2, '0');
      const ss = String(t % 60).padStart(2, '0');
      lastTime = hh + ':' + mm + ':' + ss;
    }
    ctx.fillText(lastTime, 5, 39);
  }

  function drawLeftSide() {
    const q = 16, p = 140;

    ctx.fillStyle = YELLOW;
    ctx.fillText('Lines ' + score, 5, 25);
    ctx.fillStyle = LIGHT_GRAY;
    ctx.fillText('Level ' + level, 5, 12);
    ctx.fillText('7:Left', 9, p + 2 * q);
    ctx.fillText('9:Right', 9, p + 3 * q);
    ctx.fillText('8:Rotate', 9, p + 4 * q);
    ctx.fillText('1:Draw next', 9, p + 5 * q);
    ctx.fillText('6:Faster!', 9, p + 6 * q);
    ctx.fillText('5:Slower!', 9, p + 7 * q);
    ctx.fillText('4/Spc:Drop', 0, p + 8 * q);
    drawTimer();
    ctx.fillText('Next:', 30, 330);
    ctx.fillStyle = highScoreAchieved ? fancyColor() : WHITE;
    ctx.fillText('High ' + highScore, 5, 70);
    if (gameState === PLAY && drawNextPiece) tet().drawNext(ctx);
  }

  function render() {
    ctx.font = FONT;
    ctx.textBaseline = 'alphabetic';

    drawFrame();
    drawTakenFields();

    if (gameState !== PLAY) {
      ctx.fillStyle = LIGHT_GRAY;
      if (firstTime) {
        let s = 'JT by Mihailo Despotovic';
        ctx.fillText(s, L + centered(s, 200), 120);
        // 2002: "(Click on the applet first)" -- no applet to click anymore.
        s = '(Arrows + Space to play)';
        ctx.fillText(s, L + centered(s, 200), 380);
      } else {
        ctx.fillStyle = BLACK;
        ctx.fillRect(L + 50, 142, 100, 30);
        ctx.fillStyle = LIGHT_GRAY;
        const s = 'Game Over';
        ctx.fillText(s, L + centered(s, 200), 160);
      }
      ctx.fillStyle = BLACK;
      ctx.fillRect(L + 15, 182, 170, 30);
      ctx.fillStyle = fadeColor();
      const s = 'Press any key to play';
      ctx.fillText(s, L + centered(s, 200), 200);
    } else if (paused) {
      ctx.fillStyle = BLACK;
      ctx.fillRect(L + 15, 182, 170, 30);
      ctx.fillStyle = fadeColor();
      const s = 'Paused';
      ctx.fillText(s, L + centered(s, 200), 200);
    }

    drawLeftSide();
  }

  // 2002: these advanced once per repaint. At 60fps that strobes, so they run
  // off a timer close to the applet's old repaint rate instead.
  let c1 = 0, c2 = 0, lastTick = 0;
  function animateColors(now) {
    if (now - lastTick < 120) return;
    lastTick = now;
    c1 += 20; c1 %= 255;
    c2++; c2 %= 5;
  }
  function fadeColor() {
    return 'rgb(' + c1 + ',' + c1 + ',' + c1 + ')';
  }
  function fancyColor() {
    switch (c2) {
      case 4: return '#ffc800'; // Color.orange
      case 3: return YELLOW;
      case 2: return '#ffafaf'; // Color.pink
      case 1: return '#ff00ff'; // Color.magenta
      default: return RED;
    }
  }

  // ---- loop -----------------------------------------------------------

  let acc = 0, last = 0;

  function frame(now) {
    const dt = Math.min(now - last, 100); // a backgrounded tab must not
    last = now;                           // owe us a hundred gravity ticks
    animateColors(now);

    if (gameState === PLAY && !paused) {
      acc += dt;
      while (acc >= delay) { acc -= delay; step(); }
    }

    render();
    requestAnimationFrame(frame);
  }

  // ---- setup ----------------------------------------------------------

  function resize() {
    const dpr = window.devicePixelRatio || 1;
    const fitW = (window.innerWidth - 32) / W;
    const fitH = (window.innerHeight - 150) / H;
    scale = Math.max(1, Math.min(fitW, fitH, 3));

    canvas.style.width = Math.round(W * scale) + 'px';
    canvas.style.height = Math.round(H * scale) + 'px';
    canvas.width = Math.round(W * scale * dpr);
    canvas.height = Math.round(H * scale * dpr);
    ctx.setTransform(scale * dpr, 0, 0, scale * dpr, 0, 0);
  }

  function init() {
    canvas = document.getElementById('jt');
    ctx = canvas.getContext('2d');

    loadHighScore();
    resize();
    window.addEventListener('resize', resize);
    window.addEventListener('keydown', onKeyDown);

    document.querySelectorAll('[data-action]').forEach((btn) => {
      btn.addEventListener('pointerdown', (e) => {
        e.preventDefault();
        action(btn.getAttribute('data-action'));
      });
    });

    // 2002: `private static Tetromino tet = new Tetromino();` at class init
    tet().spawn();

    last = performance.now();
    requestAnimationFrame(frame);
  }

  // Useful from the browser console, and lets the headless tests drive the
  // real line-clearing code rather than a copy of it.
  JT.debug = {
    table,
    checkAllLines,
    shiftLines,
    startGame,
    state: () => ({ gameState, paused, score, level, delay, highScore })
  };

  if (typeof document !== 'undefined') {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', init);
    } else {
      init();
    }
  }
})(window.JT);
