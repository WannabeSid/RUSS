class ClimbGauge extends Gauge
{
    PFont font;
  public ClimbGauge(int x, int y, int size)
  {
    super(x,y,size);  
    font = createFont("Arial", 32);
  }
  
  public void draw()
  {
    super.draw();
    translate(x,y);
    textAlign(CENTER,CENTER);
    fill(0);
    float metersPerSec = reading;
    textFont(font,30);
    text(metersPerSec,0,0);
    textFont(font,12);
    text("m/s",15,45);
    fill(255);
    translate(-x,-y);
  }
  
  public void update(float reading)
  {
    this.reading = reading;
  }
  
}
