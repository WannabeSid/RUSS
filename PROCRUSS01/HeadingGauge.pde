class HeadingGauge extends Gauge
{
  
  public HeadingGauge(int x, int y, int size)
  {
    super(x,y,size);
  }
  
  void update(float reading)
  {
    super.update(reading);
  }
  
  void draw()
  {
    super.draw();
    translate(x,y);
    rect(0,0,size,2);
    rect(0,0,2,size);
    rotate(radians(reading) -  PI/2);
    rect(size*1/6,0,size*1/3,4);
    rotate(-(radians(reading) -  PI/2));
    translate(-x,-y);
  }
  
  
  
}
