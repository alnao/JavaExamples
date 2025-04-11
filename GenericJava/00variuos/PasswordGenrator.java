//generatore di password
//public String generatePassword(int length, String prohibitedChars) {
//fare anche i test da lanciare con "mvn test"

import java.util.Random;
public class PasswordGenrator {
  public String generatePassword(int length, String prohibitedChars) {
    String s = "";
    for (int i = 0; i < length; i++) {
      Random randomGenerator = new Random();
      int iRand = randomGenerator.nextInt(24);//
      char codiceAsciiA = 'a';
      char cRad = (char) (iRand + codiceAsciiA);
      if (!(prohibitedChars.indexOf(cRad) > -1)) {
        s += cRad; //NON efficiente perchè String è immutabile quindi ogni volta che faccio += si crea un nuovo oggetto
      }else{
        i--;
      }
    }
    return s;
  }
}
/*
 public class TestRandomPassword{
  @Test
  public void testGenerateRandomString(){
    RandomPassword randomPasswordGenerator=new RandomPassword();
    String s=randomPasswordGenerator.generatePassword(15,"abcdef");
    System.out.println("Stringa generata=" + s);
    assertTrue( s.length() == 15 );
  }
}
 * 
 */