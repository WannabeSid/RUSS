import java.util.Stack;

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
    
    vH += .5*(distanceTo(currentAX,currentAY) - distanceTo(accel[0],accel[1]));
    
    accel[0] = currentAX;
    accel[1] = currentAY;
    accel[2] = (theData[8] - accel0[2]) - sin(radians(roll)) - sin(radians(pitch));
    vel[0] += accel[0];
    vel[1] += accel[1];
    vel[2] += accel[2];
    last20aZ.push(accel[2]);
    netV += avgAccel(last20aZ);
    System.out.println("az = " + accel[2] + "(" + (theData[8] - accel0[2]) + " - " + (sin(radians(roll)) - sin(radians(pitch))  ) );    
    System.out.println("Horizontal Accel: " + .5*(distanceTo(currentAX,currentAY) - distanceTo(accel[0],accel[1])));
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
