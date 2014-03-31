abstract class Gauge
{
  int x,y,size;
  float reading;

  public Gauge()
  {
  }
  public Gauge(int x, int y, int size)
  {
    this.x = x;
    this.y = y;
    this.size = size;
  }

  public void update(float reading)
  {
    this.reading = reading;
  }

  public void draw()
  {
    ellipseMode(CENTER);
    translate(x,y);
    ellipse(0,0,size,size);
    translate(-x,-y);
  }  
  
  
}
