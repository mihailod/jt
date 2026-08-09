import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * JT -- JavaTetris by Mihailo Despotovic
 * An old skool game ;)
 * Started: 03 April 2002
 * Core game finished: 09 April 2002
 */
public class JT extends Applet implements KeyListener
{
  private static final int L = 100;
  
  private static boolean firstTime = true;
  private static boolean drawNext = true;
  private static int highScore, score;
  private static boolean highScoreAchieved = false;
  public static final int nextX = 10;
  public static final int nextY = 350;
  private static char k = 0;

  private static final int PLAY = 1;
  private static final int GAME_OVER = 2;
  private static int gameState = GAME_OVER;
  
  private static int delay = 500;
  private static int level = 0;
  private static long startedAt = 0;
  private static Date d = null;
  private static final long timeAdjustment = 16*60*60*1000;
  private static String lastTime = "00:00:00";
  public static Point p = new Point(0, -1);
  
  private static Image buffer = null;
  private static Graphics bg = null;
  private static final Font f = new Font("DialogInput", Font.BOLD, 13);
  private static FontMetrics fm = null;
  
  private static Tetromino tet = new Tetromino();
  public static Color[][] table = new Color[10][20];
  
  public static Thread t = null;
  
	public void init()
	{
    buffer = createImage(305, 402);
    bg = buffer.getGraphics();
    bg.setFont(f);
    fm = bg.getFontMetrics();
    this.addKeyListener(this);
    
    t = new Thread(new Mover());
    t.start();
	}
  
  public void start() { ; }
  public void stop() { ; }
  public void destroy() { this.buffer = null; }
  
  private void startGame()
  {
    for(int i=0; i<10; i++)
      for(int j=0; j<20; j++)
        table[i][j] = Color.black;  
    
    highScoreAchieved = false;
    gameState = PLAY;
    startedAt = System.currentTimeMillis();
    score = 0;
  }
  
  public void paint(Graphics g) { update(g); }
  public void update(Graphics g)
  { 
    if(gameState == PLAY)
    {
      drawFrame();
      drawTakenFields();
    }
    else // game over
    {
      int offset = 0;
      drawFrame();
      drawTakenFields();
      bg.setColor(Color.lightGray);
      if(firstTime)
      {
        offset = (200 - fm.stringWidth("JT by Mihailo Despotovic")) / 2;
        bg.drawString("JT by Mihailo Despotovic", L + offset, 120);
        offset = (200 - fm.stringWidth("(Click on the applet first)")) / 2;
        bg.drawString("(Click on the applet first)", L + offset, 380);
      }
      else
      {
        bg.setColor(Color.black);
        bg.fillRect(L + 50, 142, 100, 30);
        bg.setColor(Color.lightGray);
        offset = (200 - fm.stringWidth("Game Over")) / 2;        
        bg.drawString("Game Over", L + offset, 160);
      }
      bg.setColor(Color.black);
      bg.fillRect(L + 15, 182, 170, 30);
      bg.setColor(getFadeColor());
      offset = (200 - fm.stringWidth("Press any key to play")) / 2;
      bg.drawString("Press any key to play", L + offset, 200);
    }
    drawLeftSide();
    try { g.drawImage(buffer, 0, 0, null); }
    catch(Throwable t) { ; }
  }
  
  private void drawLeftSide()
  {
    final int q = 16;
    final int p = 140;
    bg.setColor(Color.yellow);
    bg.drawString("Lines " + score, 5, 25);
    bg.setColor(Color.lightGray);
    bg.drawString("Level " + level, 5, 12);
    //bg.drawString("H E L P", 20, p/*-10*/);
    //bg.drawString("F1:Pause", 0, p+q);
    bg.drawString("7:Left", 9, p+2*q);
    bg.drawString("9:Right", 9, p+3*q);
    bg.drawString("8:Rotate", 9, p+4*q);
    bg.drawString("1:Draw next", 9, p+5*q);
    bg.drawString("6:Faster!", 9, p+6*q);
    bg.drawString("5:Slower!", 9, p+7*q);
    bg.drawString("4/Spc:Drop", 0, p+8*q);
    //bg.setColor(Color.red);
    //bg.drawString("R:Restart", 6, p+9*q);
    //bg.setColor(Color.lightGray);
    drawTimer();
    bg.drawString("Next:", 30, 330);
    bg.setColor(highScoreAchieved ? getFancyColor() : Color.white);
    bg.drawString("High " + highScore, 5, 70);
    if(gameState == PLAY && drawNext) tet.drawNext(bg);
  }
  
  private void drawTimer()
  {
    if(gameState == PLAY)
    {
      d = new Date(System.currentTimeMillis() - startedAt - timeAdjustment);
      String temp = d.toString();
      temp = temp.substring(11, 19);
      bg.drawString(temp, 5, 39);
      lastTime = temp;
    }
    else bg.drawString(lastTime, 5, 39);
  }
  
  private void drawFrame()
  {
    bg.setColor(Color.black);
    bg.fillRect(0, 0, 300, 401);
    bg.setColor(Color.red);
    bg.drawLine(L-1, 0, L-1, 400);
    bg.drawLine(L-1, 400, L + 200, 400);
    bg.drawLine(L + 200, 400, L + 200, 0);
    bg.setColor(Color.black);
  }
  
  private void drawTakenFields()
  {
    for(int i=0; i<10; i++)
      for(int j=0; j<20; j++)
      {
        bg.setColor(table[i][j]);
        bg.fillRect(L + i*20, j*20, 20, 20);
      }
  }
  
  private void checkAllLines()
  {
    // stupid, but I am lazy :| (checkLines should be more intelligent)
    checkLines(); checkLines(); checkLines(); checkLines();
  }
  
  private void checkLines()
  {
    for(int j=19; j>1; j--)
    {
      if(table[0][j] == Color.black && table[1][j] == Color.black &&
         table[2][j] == Color.black && table[3][j] == Color.black &&
         table[4][j] == Color.black && table[5][j] == Color.black &&
         table[6][j] == Color.black && table[7][j] == Color.black &&
         table[8][j] == Color.black && table[9][j] == Color.black) return;
      
      boolean line = true;
      for(int i=0; i<10; i++)
      {
        if(table[i][j] == Color.black)
        {
          line = false;
          break;
        }
      }
      if(!line) continue;
      else 
      {
        shiftLines(j);
        score++;
        Toolkit.getDefaultToolkit().beep();
        increaseLevel();
      }
    }
  }
  
  private void shiftLines(int line)
  {
    for(int i=0; i<10; i++)
      for(int j=line; j>=0; j--)
      {
        if(j == 0) table[i][j] = Color.black;
        else table[i][j] = table[i][j-1];
        repaint();
      }
  }
  
  private void increaseLevel()
  {
    if(level == 9) return;
    if(gameState == PLAY)
    {
      int lev = score / 10;
      if(level < lev)
      {
        level = lev;
        delay = 500 - 50 * lev;
      }
    }
    else
    {
      level++;
      delay = 500 - 50 * level;
    }
  }
  
  private void decreaseLevel()
  {
    if(level == 0) return;
    level--;
    delay = 500 - 50 * level;
  }
  
  public void keyPressed(KeyEvent ke)
  {
    k = ke.getKeyChar();
    
    if(k != '5' && k != '6') { if(gameState == GAME_OVER) { startGame(); return; } }
  
    if(k == '7' && tet.canMoveLeft(p.x, p.y))
    {      
      tet.draw(p.x, p.y, true);
      p.x--;
      tet.draw(p.x, p.y, false);
      repaint();
    }
    else if(k == '8' && tet.canRotate(p.x, p.y))
    { 
      tet.draw(p.x, p.y, true);
      tet.rotate();
      tet.draw(p.x, p.y, false);
      repaint();

    }
    else if(k == '9' && tet.canMoveRight(p.x, p.y))
    {
      tet.draw(p.x, p.y, true);
      p.x++;
      tet.draw(p.x, p.y, false);
      repaint();
    }
    else if(k == ' ' || k == '4')
    {
      t.stop();
      tet.draw(p.x, p.y, true);
      p.y = tet.dropPosition(p.x, p.y);
      tet.draw(p.x, p.y, false);
      checkAllLines();
      next();
      repaint();
      t = new Thread(new Mover());
      t.start();
    }
    else if(k == '1') drawNext = !drawNext;
    else if(k == '6') { if(level < 9) { level++; delay = 500 - 50 * level; } }
    else if(k == '5') { if(gameState != PLAY) decreaseLevel(); }
  }
  public void keyReleased(KeyEvent ke) { ; }
  public void keyTyped(KeyEvent ke) { ; }
  
  private void next()
  {  
    tet = new Tetromino();
    tet.draw(p.x, p.y, false);
    if(!tet.canMoveDown(p.x, p.y))
    {
      firstTime = false;
      if(gameState != GAME_OVER) Toolkit.getDefaultToolkit().beep();
      gameState = GAME_OVER;
      if(score > highScore)
      {
        highScore = score;
        highScoreAchieved = true;
      }
    }
  }
  
  private class Mover extends Thread
  { 
    private Component c = null;
    public Mover() { ; }
    
    public void run()
    {
      while(true)
      {                  
        if(gameState == PLAY)
        {
          repaint();
          try { Thread.sleep(delay); }
          catch(InterruptedException iex) { ; }
          if(tet.canMoveDown(p.x, p.y))
          { 
            tet.draw(p.x, p.y, true);
            p.y++;
            tet.draw(p.x, p.y, false);
            repaint();
          }
          else { checkAllLines(); next(); repaint(); }
        }
        else
        {
          try { Thread.sleep(delay/3); }
          catch(InterruptedException iex) { ; }
          repaint();
        }
        yield();
      }
    }
  }
  
  // color methods
  private static int c1 = 0;
  public static Color getFadeColor()
  {
    c1+=20; c1 %= 255;
    return new Color(c1, c1, c1);
  }
  
  private static int c2 = 0;
  public static Color getFancyColor()
  {
    c2++; c2 %= 5;
    switch(c2)
    {
      case 4 : return Color.orange;
      case 3 : return Color.yellow;
      case 2 : return Color.pink;
      case 1 : return Color.magenta;
      default: return Color.red;
    }
  }
}
