class PitchGauge extends Gauge
{
  int recX, recY, recW, recH;
  float reading2;
  
  public PitchGauge(int x, int y, int size)
  {
    super(x,y,size);
    recX = size/2;
    recY = size/2;
    recW = 50;
    recH = 5;
    reading2 = 0;
  }
  
  void update(float reading, float reading2)
  {
    super.update(reading);
    this.reading2 = reading2;
    //Readings come in from -90 >= angle <= 90
    float offset = map(reading,-90,90,-size/2+10,size/2-10); 
    recY = (int)offset;
  }
  
  void draw()
  {
    super.draw();
    translate(x,y);
    rotate(-radians(reading2));
    rect(0,0,size,1);
    rotate(radians(reading2));
    rect(0, recY, recW, recH);
    translate(-x,-y);
  }
}
  
    
