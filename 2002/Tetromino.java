import java.awt.*;
import java.util.*;

/**
 * Tetromino encapsulation
 */
public class Tetromino
{ 
  private static final Random generator = new Random();
  
  private static final int CUBE = 0;
  private static final int SNAKE = 1;
  private static final int ZIG = 2;
  private static final int ZAG = 3;
  private static final int T = 4;
  private static final int L = 5;
  private static final int L_INV = 6;
  
  private static final Color E = Color.black; // empty
  private static final Color COLOR_CUBE = Color.blue;
  private static final Color COLOR_SNAKE = Color.red;
  private static final Color COLOR_ZIG = Color.cyan;
  private static final Color COLOR_ZAG = Color.green;
  private static final Color COLOR_T = Color.yellow;
  private static final Color COLOR_L = Color.magenta;
  private static final Color COLOR_L_INV = Color.white;
  
  // orientations
  private static final int UP = 0;
  private static final int RIGHT = 1;
  private static final int DOWN = 2;
  private static final int LEFT = 3;
  
  private static int shape = -1;
  private static int nextShape = Math.abs(generator.nextInt() % 7);
  private static int orientation = -1;
  private static int converted = -1;
  private static int res = -1; // drop position
  
  public Tetromino()
  {
    JT.p.x = 4; JT.p.y = -1;
    orientation = LEFT;
    shape = nextShape;
    nextShape = Math.abs(generator.nextInt() % 7);
    
    // start position adjustments
    if(shape == SNAKE) JT.p.x++;
    if(shape == SNAKE || shape == CUBE) JT.p.y++;
  }
  
  public boolean canMoveLeft(int x, int y)
  {
    switch(shape)
    {
      case CUBE :
      {
        if(x > 0 &&
           JT.table[x-1][y] == E &&
           JT.table[x-1][y+1] == E) return true;
        else return false;
      }
      case SNAKE :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x > 0 &&
             JT.table[x-1][y-1] == E &&
             JT.table[x-1][y] == E &&
             JT.table[x-1][y+1] == E &&
             JT.table[x-1][y+2] == E) return true;
          else return false; 
        }
        else
        {
          if(x > 2 && JT.table[x-3][y] == E) return true;
          else return false;
        }
      }
      case ZIG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x>0 &&
             JT.table[x-1][y+1] == E && JT.table[x-1][y+2] == E &&
             JT.table[x][y] == E) return true;
          else return false;
        }
        else
        {
          if(x>0 &&
             JT.table[x-1][y+1] == E && JT.table[x][y+2] == E) return true;
          else return false;
        }
      }
      case ZAG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x>-1 &&
             JT.table[x][y] == E && JT.table[x][y+1] == E &&
             JT.table[x+1][y+2] == E) return true;
          else return false;
        }
        else
        {
          if(x>0 &&
             JT.table[x-1][y+2] == E && JT.table[x][y+1] == E) return true;
          else return false;
        }
      }
      case T:
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(x>0 &&
               JT.table[x-1][y+1] == E &&
               JT.table[x][y+2] == E) return true;
                     else return false;
          }
          case RIGHT :
          {
            if(x>-1 &&
               JT.table[x][y] == E && JT.table[x][y+1] == E &&
               JT.table[x][y+2] == E) return true;
            else return false;
          }
          case UP :
          {
            if(x>0 &&
               JT.table[x][y] == E && JT.table[x-1][y+1] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x>0 &&
               JT.table[x][y] == E && JT.table[x-1][y+1] == E &&
               JT.table[x][y+2] == E) return true;
            else return false;
          }
        } 
      }
      case L :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(x>0 &&
               JT.table[x-1][y+1] == E &&
               JT.table[x-1][y+2] == E) return true;
                     else return false;
          }
          case RIGHT :
          {
            if(x>-1 && JT.table[x][y] == E && JT.table[x][y+1] == E &&
               JT.table[x][y+2] == E) return true;
            else return false;
          }
          case UP :
          {
            if(x>0 &&
               JT.table[x+1][y] == E && JT.table[x-1][y+1] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x>0 &&
               JT.table[x-1][y] == E && JT.table[x][y+1] == E &&
               JT.table[x][y+2] == E) return true;
            else return false;
          }
        } 
      }
      case L_INV :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(x>0 &&
               JT.table[x-1][y+1] == E &&
               JT.table[x+1][y+2] == E) return true;
                     else return false;
          }
          case RIGHT :
          {
            if(x>-1 && JT.table[x][y] == E && JT.table[x][y+1] == E &&
               JT.table[x][y+2] == E) return true;
            else return false;
          }
          case UP :
          {
            if(x>0 &&
               JT.table[x-1][y] == E && JT.table[x-1][y+1] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x>0 &&
               JT.table[x][y] == E && JT.table[x][y+1] == E &&
               JT.table[x-1][y+2] == E) return true;
            else return false;
          }
        } 
      }
    }
    return false;
  }
  
  public boolean canMoveRight(int x, int y)
  {
    switch(shape)
    {
      case CUBE :
      {
        if(x < 8 &&
           JT.table[x+2][y] == E &&
           JT.table[x+2][y+1] == E) return true;
        else return false;
      }
      case SNAKE :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x < 9 &&
             JT.table[x+1][y-1] == E &&
             JT.table[x+1][y] == E &&
             JT.table[x+1][y+1] == E &&
             JT.table[x+1][y+2] == E) return true;
          else return false;
        }
        else
        { 
          if(x < 8 && JT.table[x+2][y] == E) return true;
          else return false;
        }
      }
      case ZIG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x<8 &&
             JT.table[x+2][y] == E && JT.table[x+2][y+1] == E &&
             JT.table[x+1][y+2] == E) return true;
          else return false;
        }
        else
        {
          if(x<7 &&
             JT.table[x+2][y+1] == E && JT.table[x+3][y+2] == E) return true;
          else return false;
        }
      }
      case ZAG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x<7 &&
             JT.table[x+2][y] == E && JT.table[x+3][y+1] == E &&
             JT.table[x+3][y+2] == E) return true;
          else return false;
        }
        else
        {
          if(x<7 &&
             JT.table[x+3][y+1] == E && JT.table[x+2][y+2] == E) return true;
          else return false;
        }
      }
      case T:
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(x<7 &&
               JT.table[x+3][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
                     else return false;
          }
          case RIGHT :
          {
            if(x<7 &&
               JT.table[x+2][y] == E && JT.table[x+3][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case UP :
          {
            if(x<7 &&
               JT.table[x+2][y] == E && JT.table[x+3][y+1] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x<8 &&
               JT.table[x+2][y] == E && JT.table[x+2][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
        }
      }
      case L :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(x<7 &&
               JT.table[x+3][y+1] == E &&
               JT.table[x+1][y+2] == E) return true;
                     else return false;
          }
          case RIGHT :
          {
            if(x<7 &&
               JT.table[x+2][y] == E && JT.table[x+2][y+1] == E &&
               JT.table[x+3][y+2] == E) return true;
            else return false;
          }
          case UP :
          {
            if(x<7 &&
               JT.table[x+3][y] == E && JT.table[x+3][y+1] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x<8 &&
               JT.table[x+2][y] == E && JT.table[x+2][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
        }
      }
      case L_INV :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(x<7 &&
               JT.table[x+3][y+1] == E &&
               JT.table[x+3][y+2] == E) return true;
                     else return false;
          }
          case RIGHT :
          {
            if(x<7 &&
               JT.table[x+3][y] == E && JT.table[x+2][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case UP :
          {
            if(x<7 &&
               JT.table[x+1][y] == E && JT.table[x+3][y+1] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x<8 &&
               JT.table[x+2][y] == E && JT.table[x+2][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
        }
      }
    }
    return false;
  }
  
  public boolean canMoveDown(int x, int y)
  {
    switch(shape)
    {
      case CUBE :
      {
        if(y < 18 &&
           JT.table[x][y+2] == E &&
           JT.table[x+1][y+2] == E) return true;
        else return false;
                  
      }
      case SNAKE :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(y < 17 &&
             JT.table[x][y+3] == E) return true;
          else return false;
        }
        else
        {
          if(y<19 &&
             JT.table[x-2][y+1] == E && JT.table[x-1][y+1] == E &&
             JT.table[x][y+1] == E && JT.table[x+1][y+1] == E) return true;
          else return false;
        }
      }
      case ZIG:
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(y<17 &&
             JT.table[x][y+3] == E && JT.table[x+1][y+2] == E) return true;
          else return false;
        }
        else
        {
          if(y<17 &&
             JT.table[x][y+2] == E && JT.table[x+1][y+3] == E &&
             JT.table[x+2][y+3] == E) return true;
          else return false;
        }
      }
      case ZAG:
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(y<17 &&
             JT.table[x+1][y+2] == E && JT.table[x+2][y+3] == E) return true;
          else return false;
        }
        else
        {
          if(y<17 &&
             JT.table[x][y+3] == E && JT.table[x+1][y+3] == E &&
             JT.table[x+2][y+2] == E) return true;
          else return false;
        }
      }
      case T :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(y<17 &&
               JT.table[x][y+2] == E && JT.table[x+1][y+3] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case RIGHT :
          {
            if(y<17 &&
               JT.table[x+1][y+3] == E && JT.table[x+2][y+2] == E) return true;
            else return false;
          }            
          case UP :
          {
            if(y<18 &&
               JT.table[x][y+2] == E && JT.table[x+1][y+2] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(y<17 &&
               JT.table[x][y+2] == E && JT.table[x+1][y+3] == E) return true;
            else return false;
          }
        }
      }
      case L :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(y<17 &&
               JT.table[x][y+3] == E && JT.table[x+1][y+2] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case RIGHT :
          {
            if(y<17 &&
               JT.table[x+1][y+3] == E && JT.table[x+2][y+3] == E) return true;
            else return false;
          }            
          case UP :
          {
            if(y<18 &&
               JT.table[x][y+2] == E && JT.table[x+1][y+2] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(y<17 &&
               JT.table[x][y+1] == E && JT.table[x+1][y+3] == E) return true;
            else return false;
          } 
        }
      }
      case L_INV :
      {
        converted = this.convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(y<17 &&
               JT.table[x][y+2] == E && JT.table[x+1][y+2] == E &&
               JT.table[x+2][y+3] == E) return true;
            else return false;
          }
          case RIGHT :
          {
            if(y<17 &&
               JT.table[x+1][y+3] == E && JT.table[x+2][y+1] == E) return true;
            else return false;
          }            
          case UP :
          {
            if(y<18 &&
               JT.table[x][y+2] == E && JT.table[x+1][y+2] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(y<17 &&
               JT.table[x][y+3] == E && JT.table[x+1][y+3] == E) return true;
            else return false;
          } 
        }
      }
      default : return true;
    }
  }
  
  public int dropPosition(int x, int y)
  {
    switch(shape)
    {
      case CUBE :
      { 
        res = 18;
        for(int i=y; i<18; i++)
        {
          if(JT.table[x][i+2] != E ||
             JT.table[x+1][i+2] != E)
          {
            res = i;
            break;
          }
        }
        return res;
      }
      case SNAKE :
      {
        if(orientation == UP || orientation == DOWN)
        {
          res = 17;
          for(int i=y; i<17; i++)
          {
            if(JT.table[x][i+3] != E)
            {
              res = i;
              break;
            }
          }
          return res;
        }
        else
        {
          res = 19;
          for(int i=y; i<19; i++)
          {
            if(JT.table[x-2][i+1] != E ||
               JT.table[x-1][i+1] != E ||
               JT.table[x][i+1] != E ||
               JT.table[x+1][i+1] != E)
            {
              res = i;
              break;
            }
          }
          return res;          
        }
      }
      case ZIG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          res = 17;
          for(int i=y; i<17; i++)
          {
            if(JT.table[x][i+3] != E || JT.table[x+1][i+2] != E)
            {
              res = i;
              break;
            }
          }
          return res;
        }
        else
        {
          res = 17;
          for(int i=y; i<17; i++)
          {
            if(JT.table[x][i+2] != E ||
               JT.table[x+1][i+3] != E ||
               JT.table[x+2][i+3] != E)
            {
              res = i;
              break;
            }
          }
          return res;          
        }
      }
      case ZAG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          res = 17;
          for(int i=y; i<17; i++)
          {
            if(JT.table[x+1][i+2] != E || JT.table[x+2][i+3] != E)
            {
              res = i;
              break;
            }
          }
          return res;
        }
        else
        {
          res = 17;
          for(int i=y; i<17; i++)
          {
            if(JT.table[x][i+3] != E ||
               JT.table[x+1][i+3] != E ||
               JT.table[x+2][i+2] != E)
            {
              res = i;
              break;
            }
          }
          return res;          
        }
      }
      case T :
      {
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x][i+2] != E ||
                 JT.table[x+1][i+3] != E ||
                 JT.table[x+2][i+2] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case RIGHT :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x+1][i+3] != E ||
                 JT.table[x+2][i+2] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case UP :
          {
            res = 18;
            for(int i=y; i<18; i++)
            {
              if(JT.table[x][i+2] != E ||
                 JT.table[x+1][i+2] != E ||
                 JT.table[x+2][i+2] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case LEFT :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x][i+2] != E ||
                 JT.table[x+1][i+3] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
        }
      }
      case L :
      {
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x][i+3] != E ||
                 JT.table[x+1][i+2] != E ||
                 JT.table[x+2][i+2] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case RIGHT :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x+1][i+3] != E ||
                 JT.table[x+2][i+3] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case UP :
          {
            res = 18;
            for(int i=y; i<18; i++)
            {
              if(JT.table[x][i+2] != E ||
                 JT.table[x+1][i+2] != E ||
                 JT.table[x+2][i+2] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case LEFT :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x][i+1] != E ||
                 JT.table[x+1][i+3] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
        }
      }
      case L_INV :
      {
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x][i+2] != E ||
                 JT.table[x+1][i+2] != E ||
                 JT.table[x+2][i+3] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case RIGHT :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x+1][i+3] != E ||
                 JT.table[x+2][i+1] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case UP :
          {
            res = 18;
            for(int i=y; i<18; i++)
            {
              if(JT.table[x][i+2] != E ||
                 JT.table[x+1][i+2] != E ||
                 JT.table[x+2][i+2] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
          case LEFT :
          {
            res = 17;
            for(int i=y; i<17; i++)
            {
              if(JT.table[x][i+3] != E ||
                 JT.table[x+1][i+3] != E)
              {
                res = i;
                break;
              }
            }
            return res;  
          }
        }        
      }
    }
    if(true) throw new IllegalArgumentException("What?");
    return -1;
  }
  
  public boolean canRotate(int x, int y)
  {
    switch(shape)
    {
      case CUBE : return false;
      case SNAKE :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x<9 && x>1 &&
             JT.table[x-2][y] == E &&
             JT.table[x-1][y] == E &&
             JT.table[x+1][y] == E) return true;
          else return false;
        }
        else
        {
          if(y>0 &&
             JT.table[x][y-1] == E &&
             JT.table[x][y+1] == E &&
             JT.table[x][y+2] == E) return true;
          else return false;
        }
      }
      case ZIG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x<8 &&
             JT.table[x+1][y+2] == E && JT.table[x+2][y+2] == E) return true;
          else return false;
        }
        else
        {
          if(y>-1 &&
             JT.table[x][y+2] == E && JT.table[x+1][y] == E) return true;
          else return false;
        }
      }
      case ZAG :
      {
        if(orientation == UP || orientation == DOWN)
        {
          if(x<8 && x>-1 &&
             JT.table[x][y+2] == E && JT.table[x+1][y+2] == E) return true;
          else return false;
        }
        else
        {
          if(y>-1 &&
             JT.table[x+1][y] == E && JT.table[x+2][y+2] == E) return true;
          else return false;
        }
      }
      case T :
      {
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN : if(y>-1 && JT.table[x+1][y] == E) return true; else return false;
          case RIGHT : if(x>-1 && JT.table[x][y+1] == E) return true; else return false;
          case UP: if(JT.table[x+1][y+2] == E) return true; else return false;
          case LEFT : if(x<8 && JT.table[x+2][y+1] == E) return true; else return false;
        }
      }
      case L :
      {
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(y>-1 && JT.table[x+1][y] == E && JT.table[x+1][y+2] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
          case RIGHT :
          {
            if(y>-1 && x>-1 && JT.table[x+2][y] == E && JT.table[x+2][y+1] == E &&
               JT.table[x][y+1] == E) return true;
            else return false;
          }
          case UP :
          {
            if(y<19 && y>-1 &&
               JT.table[x][y] == E && JT.table[x+1][y] == E &&
               JT.table[x+1][y+2] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x<8 && y>-1 &&
               JT.table[x+2][y+1] == E && JT.table[x][y+1] == E &&
               JT.table[x][y+2] == E) return true;
            else return false;
          }
        }
      }
      case L_INV :
      {
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            if(y>-1 && JT.table[x+1][y] == E && JT.table[x+2][y] == E &&
               JT.table[x+1][y+2] == E) return true;
            else return false;
          }
          case RIGHT :
          {
            if(y>-1 && x>-1 && JT.table[x][y] == E && JT.table[x][y+1] == E &&
               JT.table[x+2][y+1] == E) return true;
            else return false;
          }
          case UP :
          {
            if(y<19 && y > -1 &&
               JT.table[x+1][y] == E && JT.table[x][y+2] == E &&
               JT.table[x+1][y+2] == E) return true;
            else return false;
          }
          case LEFT :
          {
            if(x<8 &&
               JT.table[x][y+1] == E && JT.table[x+2][y+1] == E &&
               JT.table[x+2][y+2] == E) return true;
            else return false;
          }
        }
      }
    }
    return false;
  }
  
  public void rotate()
  {
    if(shape == CUBE) return;
    orientation++; orientation %= 4;
  }
  
  public void draw(int x, int y, boolean erase)
  {
    Color c = null;
    switch(shape)
    {
      case CUBE :
      { 
        if(erase) c = E; else c = COLOR_CUBE;
        JT.table[x][y] = c; JT.table[x+1][y] = c;
        JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
        break;
      }
      case SNAKE :
      {
        if(erase) c = E; else c = COLOR_SNAKE;
        if(orientation == UP || orientation == DOWN)
        {
          JT.table[x][y-1] = c; JT.table[x][y] = c;
          JT.table[x][y+1] = c; JT.table[x][y+2] = c;
        }
        else
        {
          JT.table[x-2][y] = c; JT.table[x-1][y] = c;
          JT.table[x][y] = c; JT.table[x+1][y] = c;            
        }
        break;
      }
      case ZIG :
      {
        if(erase) c = E; else c = COLOR_ZIG;
        if(orientation == UP || orientation == DOWN)
        {
          JT.table[x+1][y] = c; JT.table[x+1][y+1] = c;
          JT.table[x][y+1] = c; JT.table[x][y+2] = c;
        }
        else
        {
          JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
          JT.table[x+1][y+2] = c; JT.table[x+2][y+2] = c;
        }
        break;
      }
      case ZAG :
      {
        if(erase) c = E; else c = COLOR_ZAG;
        if(orientation == UP || orientation == DOWN)
        {
          JT.table[x+1][y] = c; JT.table[x+1][y+1] = c;
          JT.table[x+2][y+1] = c; JT.table[x+2][y+2] = c;
        }
        else
        {
          JT.table[x+1][y+1] = c; JT.table[x+2][y+1] = c;
          JT.table[x][y+2] = c; JT.table[x+1][y+2] = c;
        }
        break;
      }
      case T :
      {
        if(erase) c = E; else c = COLOR_T;
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN:
          {
            JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
            JT.table[x+2][y+1] = c; JT.table[x+1][y+2] = c;
            break;
          }
          case RIGHT:
          {
            JT.table[x+1][y] = c; JT.table[x+1][y+1] = c;
            JT.table[x+2][y+1] = c; JT.table[x+1][y+2] = c;
            break;              
          }
          case UP:
          {
            JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
            JT.table[x+2][y+1] = c; JT.table[x+1][y] = c;
            break;              
          }
          case LEFT:
          {
            JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
            JT.table[x+1][y] = c; JT.table[x+1][y+2] = c;
            break;              
          }
        }
        break;
      } 
      case L :
      {
        if(erase) c = E; else c = COLOR_L;
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
            JT.table[x+2][y+1] = c; JT.table[x][y+2] = c;
            break;
          }
          case RIGHT :
          {
            JT.table[x+1][y] = c; JT.table[x+1][y+1] = c;
            JT.table[x+1][y+2] = c; JT.table[x+2][y+2] = c;
            break;              
          }
          case UP :
          {
            JT.table[x+2][y] = c; JT.table[x][y+1] = c;
            JT.table[x+1][y+1] = c; JT.table[x+2][y+1] = c;
            break;
          }
          case LEFT :
          {
            JT.table[x][y] = c; JT.table[x+1][y] = c;
            JT.table[x+1][y+1] = c; JT.table[x+1][y+2] = c;
            break;
          }
        }
        break;
      }
      case L_INV :
      {
        if(erase) c = E; else c = COLOR_L_INV;
        converted = convertOrientation();
        switch(converted)
        {
          case DOWN :
          {
            JT.table[x][y+1] = c; JT.table[x+1][y+1] = c;
            JT.table[x+2][y+1] = c; JT.table[x+2][y+2] = c;
            break;
          }
          case RIGHT :
          {
            JT.table[x+1][y] = c; JT.table[x+2][y] = c;
            JT.table[x+1][y+1] = c; JT.table[x+1][y+2] = c;
            break;              
          }
          case UP :
          {
            JT.table[x][y] = c; JT.table[x][y+1] = c;
            JT.table[x+1][y+1] = c; JT.table[x+2][y+1] = c;
            break;
          }
          case LEFT :
          {
            JT.table[x+1][y] = c; JT.table[x+1][y+1] = c;
            JT.table[x+1][y+2] = c; JT.table[x][y+2] = c;
            break;
          }
        }
      }
    }
  }
  
  public void drawNext(Graphics g)
  {
    switch(nextShape)
    {
      case SNAKE :
      {
        g.setColor(COLOR_SNAKE);
        g.fillRect(JT.nextX, JT.nextY, 80, 20);
        break;
      }
      case CUBE :
      {
        g.setColor(COLOR_CUBE);
        g.fillRect(JT.nextX + 20, JT.nextY, 40, 40);
        break;
      }
      case T :
      {
        g.setColor(COLOR_T);
        g.fillRect(JT.nextX + 20, JT.nextY, 60, 20);
        g.fillRect(JT.nextX + 40, JT.nextY + 20, 20, 20);
        break;
      }
      case ZIG :
      {
        g.setColor(COLOR_ZIG);
        g.fillRect(JT.nextX + 20, JT.nextY, 40, 20);
        g.fillRect(JT.nextX + 40, JT.nextY + 20, 40, 20);
        break;
      }
      case ZAG :
      {
        g.setColor(COLOR_ZAG);
        g.fillRect(JT.nextX + 40, JT.nextY, 40, 20);
        g.fillRect(JT.nextX + 20, JT.nextY + 20, 40, 20);
        break;
      }
      case L :
      {
        g.setColor(COLOR_L);
        g.fillRect(JT.nextX + 20, JT.nextY, 60, 20);
        g.fillRect(JT.nextX + 20, JT.nextY + 20, 20, 20);
        break;
      }
      case L_INV :
      {
        g.setColor(COLOR_L_INV);
        g.fillRect(JT.nextX + 20, JT.nextY, 60, 20);
        g.fillRect(JT.nextX + 60, JT.nextY + 20, 20, 20);
        break;
      }
    }
  }

  /**
   * URDL -> DRUL
   */
  private static int convertOrientation()
  {
    switch(orientation)
    {
      case LEFT : return DOWN;
      case UP : return RIGHT;         
      case RIGHT : return UP;
      case DOWN : return LEFT;
      default : return -1; // should never happen
    }
  }
}
