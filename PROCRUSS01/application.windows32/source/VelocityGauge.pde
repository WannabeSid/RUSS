class VelocityGauge extends Gauge
{
  PFont font;
  
  public VelocityGauge(int x, int y, int size)
  {
    super(x,y,size);  
    font = createFont("Arial", 32);
  }
  
  public void update(float reading)
  {
    super.update(reading);
  }
  
  public void draw()
  {
    super.draw();
    translate(x,y);
    textAlign(CENTER,CENTER);
    fill(0);
    textFont(font,30);
    text(reading,0,0);
    textFont(font,12);
    text("net A",15,20);
    fill(255);
    translate(-x,-y);
  }
}
