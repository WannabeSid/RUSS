class MotorGauge extends Gauge
{
  boolean reverse;
  
  public MotorGauge(int x, int y, int size)
  {
    super(x,y,size);  
    reverse = false;
  }
  
  public void draw()
  {
    super.draw();
    translate(x,y);
    if(reverse)
    {
      for(int i = 0; i > reading; i--)
      {
        fill(200,15,15);
        arc(0,0,size,size,radians((i-1)*36)+PI,radians(i*36)+PI);
      }      
    }
    else
    {
      for(int i = 0; i < reading; i++)
      {
        fill(15,200,15);
        arc(0,0,size,size,radians(i*36)+PI,radians((i+1)*36)+PI);
      }
    }
    fill(255);
    translate(-x,-y);
  }
  
  public void update(float reading)
  {
    super.update(reading);
    if(reading < 0)
    {
      reverse = true;
    }
    else if(reading > 0)
    {
      reverse = false;
    }
    if(reading > 10)
      this.reading = 10;
    if(reading < -10)
      this.reading = -10;
  }
  
}
