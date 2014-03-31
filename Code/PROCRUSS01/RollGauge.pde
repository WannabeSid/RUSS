class RollGauge extends Gauge
{
  
  public RollGauge(int x, int y, int size)
  {
    super(x,y,size);  
  }
  
  public void draw()
  {
    super.draw();
    translate(x,y);
    rotate(-radians(reading));
    rect(0,-5,size/3,5);
    rect(0,0,size*2/3,5);
    rotate(radians(reading));
    translate(-x,-y);
  }
  
  public void update(float reading)
  {
    super.update(reading);
  }
  
}
