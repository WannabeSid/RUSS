import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import java.util.StringTokenizer; 
import processing.video.*; 
import java.util.Stack; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PROCRUSS01 extends PApplet {






Capture cam;
Serial port;
PrintWriter output;
float[] theData;
Compass compass;
PitchGauge pitchGauge;
HeadingGauge headingGauge;
RollGauge rollGauge;
VelocityGauge velocityGauge;
DepthGauge depthGauge;
ClimbGauge climbGauge;
MotorGauge motorGaugeL1;
MotorGauge motorGaugeL2;
MotorGauge motorGaugeR1;
MotorGauge motorGaugeR2;
float lastA;
float lastAZ;

public void setup()
{
  size(1000,700);
  output = createWriter("data.csv");
  output.println("HEADING,PITCH,ROLL,TEMP,DEPTH,Ax,Ay,Az,Vh,NetV,NetA");
  port = new Serial(this, Serial.list()[0], 57600);
  port.bufferUntil('\n');
  theData = new float[10];
  pitchGauge = new PitchGauge(325,540,100);
  velocityGauge = new VelocityGauge(220,540,100);
  headingGauge = new HeadingGauge(325,645,100);
  rollGauge = new RollGauge(220,645,100);
  depthGauge = new DepthGauge(430,540,100);
  climbGauge = new ClimbGauge(430,645,100);
  compass = new Compass();
  motorGaugeL1 = new MotorGauge(40,590,75);
  motorGaugeL2 = new MotorGauge(120,590,75);
  motorGaugeR1 = new MotorGauge(535,590,75);
  motorGaugeR2 = new MotorGauge(615,590,75);
  lastA = 0;
  lastAZ = 0;
  port.clear();
  noLoop();
  //cam = new Capture(this, Capture.list()[0]);
  //cam.start();     
  
  
}

public String dataToString()
{
  String result = "";
  for(int i = 0; i < 8; i++)
  {
    if(i != 7)
      result += theData[i] + ",";     
    else
      result += ""+theData[i];
  }
  return result;
}

public void draw()
{
  background(100);
  rectMode(CORNER);
  rect(5,5,640,480);
  rectMode(CENTER);
  ellipseMode(CENTER);
  pitchGauge.update(compass.pitch, compass.roll);
  headingGauge.update(compass.heading);
  rollGauge.update(compass.roll);
  velocityGauge.update(compass.netA);
  climbGauge.update(compass.accel[2]);
  rollGauge.draw();
  pitchGauge.draw();
  headingGauge.draw();
  velocityGauge.draw();
  depthGauge.draw();
  climbGauge.draw();
  motorGaugeL1.draw();
  motorGaugeL2.draw();
  motorGaugeR1.draw();
  motorGaugeR2.draw();  
  //if (cam.available() == true) {
  //  cam.read();
  //}
  //image(cam, 5, 5    );
  output.println(compass.toString());
}

public void setData(String data)
{
  StringTokenizer tok = new StringTokenizer(data,"CTRP*xyzA$D");
  for(int i = 0; tok.hasMoreTokens(); i++)
  {
    String temp = tok.nextToken();
    if(Character.isLetter(temp.charAt(0)) || temp.charAt(0) == '$')
    {
      i--;
    }
    else
    {
      System.out.print("([" + i +"]=" + temp +")");
      theData[i] = (float)Float.parseFloat(temp);
    }
  }
  compass.update(theData);
}

public void serialEvent(Serial p)
{
  try
  {
  String buf = p.readString();
  System.out.println(buf.substring(1,buf.length()-4));
  //$C207.5P4.3R116.9T26.9D0.0000Ax0.075Ay0.894Az-0.450*13
  //Compass, Pitch, Roll, Temp, Depth, Ax, Ay, Az, Parity
  if(buf.charAt(0) == '$')
    setData(buf.substring(1,buf.length()-4));
  //System.out.println(theData[0]);
  redraw();
  }
  catch(Exception e)
  {
    System.out.println("OOPS");
    e.printStackTrace();
  }
}

public void keyPressed()
{
  if(key == 's')
  {
    output.flush();
    output.close();
    exit();
  }
  if(keyCode == UP)
  {
    motorGaugeL1.update(motorGaugeL1.reading+1);
    motorGaugeL2.update(motorGaugeL2.reading+1);
    motorGaugeR1.update(motorGaugeR1.reading+1);
    motorGaugeR2.update(motorGaugeR2.reading+1);
  }
  if(keyCode == DOWN)
  {
    motorGaugeL1.update(motorGaugeL1.reading-1);
    motorGaugeL2.update(motorGaugeL2.reading-1);
    motorGaugeR1.update(motorGaugeR1.reading-1);
    motorGaugeR2.update(motorGaugeR2.reading-1);
  }
  if(keyCode == LEFT)
  {
    motorGaugeL1.update(motorGaugeL1.reading+1);
    motorGaugeL2.update(motorGaugeL2.reading+1);
    motorGaugeR1.update(motorGaugeR1.reading-1);
    motorGaugeR2.update(motorGaugeR2.reading-1);
  }
  if(keyCode == RIGHT)
  {
    motorGaugeL1.update(motorGaugeL1.reading-1);
    motorGaugeL2.update(motorGaugeL2.reading-1);
    motorGaugeR1.update(motorGaugeR1.reading+1);
    motorGaugeR2.update(motorGaugeR2.reading+1);
  }
  if(key == ' ')  
  {
    motorGaugeL1.update(0);
    motorGaugeL2.update(0);
    motorGaugeR1.update(0);
    motorGaugeR2.update(0);
  }
}
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


class Compass
{
  float heading, heading0;
  float pitch, pitch0;
  float roll, roll0;
  float velocity;
  float depth;
  float velocityZ;
  float[] accel, accel0;
  float[] vel, vel0;
  float netA, netA0;
  float vH;
  float netV;
  
  Stack<Float> last20aZ;
  float lastA;
  
  boolean firstTime;
  
  
  
  public Compass()
  {
    heading = 0;
    pitch = 0;
    roll = 0;
    depth = 0;
    velocity = 0;
    velocityZ = 0;
    lastA = 0;
    accel = new float[3];
    accel0 = new float[3];
    vel = new float[3];
    vel0 = new float[3];
    firstTime = true;
    vH = 0;
    netV = 0;
    last20aZ = new Stack<Float>();
  }
  
  public void update(float[] theData)
  {
    if(firstTime)
      zero(theData);
    if(last20aZ.size() > 20)
      last20aZ.pop();
    heading = theData[0];
    pitch = theData[1] - pitch0;
    roll = theData[2] - roll0;
    netA = (theData[5]-netA0);
    float currentAX = ((theData[6] - accel0[0]) - (sin(radians(pitch))));
    System.out.println("ax = " + currentAX + "(" + (theData[6] - accel0[0]) + " - " + sin(radians(pitch)) + ")");
    
    float currentAY = ((theData[7] - accel0[1]) - (sin(radians(roll))));
    System.out.println("ay = " + currentAY + "(" + (theData[7] - accel0[1]) + " - " + sin(radians(roll)) + ")");
    
    vH += .5f*(distanceTo(currentAX,currentAY) - distanceTo(accel[0],accel[1]));
    
    accel[0] = currentAX;
    accel[1] = currentAY;
    accel[2] = (theData[8] - accel0[2]) - sin(radians(roll)) - sin(radians(pitch));
    vel[0] += accel[0];
    vel[1] += accel[1];
    vel[2] += accel[2];
    last20aZ.push(accel[2]);
    netV += avgAccel(last20aZ);
    System.out.println("az = " + accel[2] + "(" + (theData[8] - accel0[2]) + " - " + (sin(radians(roll)) - sin(radians(pitch))  ) );    
    System.out.println("Horizontal Accel: " + .5f*(distanceTo(currentAX,currentAY) - distanceTo(accel[0],accel[1])));
    System.out.println("Net Accel: " + netA);
    System.out.println("AVG NET ACCEL: " + avgAccel(last20aZ)); 
    System.out.println("X Velocity: " + vel[0]);   
    System.out.println("Y Velocity: " + vel[1]);   
    System.out.println("Z Velocity: " + vel[2]);   
    System.out.println("Horizontal Velocity: " + vH);
    System.out.println("Net Velocity: " + netV);
  }
  
  public void zero(float[] theData)
  {
    firstTime = false;
    heading0 = theData[0];
    netA0 = theData[5];
    pitch0 = theData[1];
    roll0 = theData[2];
    accel0[0] = theData[6];
    accel0[1] = theData[7];
    accel0[2] = theData[8];
    vel0[0] = 0;
    vel0[1] = 0;
    vel0[2] = 0;
  }
  
  public float avgAccel(Stack accel)
  {
     Stack temp = (Stack<Float>)accel.clone(); 
     float total = 0;
     float count = 0;
     while(!temp.isEmpty())
     {
       total += (Float)temp.pop();
       count++;
     }
     return total/count;
  }
  
  public float distanceTo(float x, float y)
  {
    return (float)Math.sqrt(x*x + y*y);  
  }
  
  public String toString()
  {
    String result = heading + "," + pitch + "," + roll + "," + 0 + "," + depth 
        + "," + accel[0] + "," + accel[1] + "," + accel[2] + "," + vH + "," + netV + "," + netA;
    return result;
  }
}
class DepthGauge extends Gauge
{
  
  public DepthGauge(int x, int y, int size)
  {
    super(x,y,size);  
  }
  
  public void draw()
  {
    super.draw();
  }
  
  public void update(float reading)
  {
    super.update(reading);
  }
  
}
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
class HeadingGauge extends Gauge
{
  
  public HeadingGauge(int x, int y, int size)
  {
    super(x,y,size);
  }
  
  public void update(float reading)
  {
    super.update(reading);
  }
  
  public void draw()
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
  
  public void update(float reading, float reading2)
  {
    super.update(reading);
    this.reading2 = reading2;
    //Readings come in from -90 >= angle <= 90
    float offset = map(reading,-90,90,-size/2+10,size/2-10); 
    recY = (int)offset;
  }
  
  public void draw()
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PROCRUSS01" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
