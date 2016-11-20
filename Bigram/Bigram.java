import java.util.Scanner;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays.*;
import java.util.*;
import java.io.*;



public class Bigram {
  String sentence;
  String[] sentenceArray;
  HashMap<String, Integer> tokens = new HashMap<String, Integer>();
  HashMap<String, Integer> tokensCount = new HashMap<String, Integer>();
  HashMap<String, Integer> tokensKey = new HashMap<String, Integer>();
  HashMap<String, Integer> goodTuringCount = new HashMap<String, Integer>();
  HashMap<String, Integer> countMap = new HashMap<String, Integer>();
  //HashMap<Double, Double> probC= new HashMap<Double,Double>();
  //gives the name of the tokens
  String[] keyArray;
  int num = 0;
  int countN = 0;// int[] counts;
  //gives table of counts of simple counts given that etc.
  int[][] counter;
  //gives table for add one counts
  int[][] counterAddOneSmoothing;
  //gives table of good turing counts
  float[][] counterGoodTuring;


  Bigram (){

  }
  Bigram (int num) {
    this.num = num;
  }


  public void CreateSentence(){
    Scanner in = new Scanner(System. in);
    System.out.print("Enter string " + num + ": ");
    sentence = in.nextLine();
    sentenceArray = sentence.split(" ");
    int l1 = sentenceArray.length - 1;
    int l2 = sentenceArray[l1].length() - 1;
    if(sentenceArray[l1].charAt(l2) == '.') {
        // System.out.println("dot found " + sentenceArray[l1] + "   " + sentenceArray[l1].substring(0, l2));
        sentenceArray[l1] = sentenceArray[l1].substring(0, l2);
    }
    // System.out.println(String.join(" ", sentenceArray));
  }

  public void CreateSentence2(String s){
	    sentence = s;
	    sentenceArray = sentence.split(" ");
	    int l1 = sentenceArray.length - 1;
	    int l2 = sentenceArray[l1].length() - 1;
	    if(sentenceArray[l1].charAt(l2) == '.') {
	        // System.out.println("dot found " + sentenceArray[l1] + "   " + sentenceArray[l1].substring(0, l2));
	        sentenceArray[l1] = sentenceArray[l1].substring(0, l2);
	    }
	    // System.out.println(String.join(" ", sentenceArray));

  }

  private void getTokens() {
    String[] strArray = sentence.split(" ");
    int numOfUniqueTokens = 0;
    for(String i : sentenceArray) {
      // System.out.println(i);
      if(tokens.containsKey(i)) {
        tokens.put(i, tokens.get(i) + 1);
        // System.out.println("putting " + i + "  with count " +  tokens.get(i));


      } else {
        tokens.put(i, 1);
        // System.out.println("putting " + i + "  with count " +  tokens.get(i));

        numOfUniqueTokens++;
      }
    }

    Set<String> keys = tokens.keySet();
    keyArray = keys.toArray(new String[keys.size()]);
    // System.out.println("number of unique : " + numOfUniqueTokens + " and value at am is " + tokens.get("am"));
    // System.out.println("key array : " + String.join(" ", array ));
    // System.out.println("key array : " + keyArray[0]);
    counter = new int[numOfUniqueTokens][numOfUniqueTokens];
    counterAddOneSmoothing = new int[numOfUniqueTokens][numOfUniqueTokens];
    counterGoodTuring      = new float[numOfUniqueTokens][numOfUniqueTokens];
    // counts = new int[numOfUniqueTokens];
    // Arrays.fill(counter, 0);
    int i = 0;
    for(String k : keyArray) {
      tokensKey.put(k, i);
      i++;
    }

  }

  private void display() {

    //read line by line
  BufferedReader reader = null;
  String line = "";
  String tokenUnderTest = "";
  String restLine = "";

  try {
        reader = new BufferedReader( new FileReader("NLPCorpusTreebank2Parts-CorpusA-Unix.txt"));
        boolean start = true;
        while( (line = reader.readLine()) != null){
          if(start) {
            restLine = line;
            start = false;
          } else {
            restLine += " " + line;
          }

          int oldIndex = 0;
          int currIndex;

          while( restLine != null) {
            currIndex = restLine.indexOf(" . ");
            if(currIndex == -1) {
              break;
            }

            tokenUnderTest = restLine.substring(0, currIndex);

            restLine = restLine.substring(currIndex + 2, restLine.length());
            // System.out.println("token under test is " + tokenUnderTest + "\n rest is-" + restLine);
            process(tokenUnderTest);
            oldIndex = currIndex + 1;
          }
        }
        process(restLine);
       } catch (FileNotFoundException e) {
           System.err.println("Unable to find the file: fileName");
       } catch (IOException e) {
           System.err.println("Unable to read the file: fileName");
       }
         show(counter);
         showProbability(counter);
         addOneSmoothing();
         showAddOne(counterAddOneSmoothing);
         showAddOneSmoothingProbability(counterAddOneSmoothing);
         goodTuringCalculator(goodTuringCount);
         showGoodTuringCount(counterGoodTuring);
         showGoodTuringProbability(counterGoodTuring);
    //create string

    //send to process

  }

//issue checks only one instance of match can be multiple keys
  private void process(String line) {

    String[] lineArray = line.split(" +");

    for(int i = 0; i < lineArray.length; i++ ) {
      /////////////////////added code
    	//System.out.println("Line array elements");
    	//System.out.println(lineArray[i]);
       if(goodTuringCount.containsKey(lineArray[i])) {
        goodTuringCount.put(lineArray[i], goodTuringCount.get(lineArray[i]) + 1);
        // System.out.println("putting " + i + "  with count " +  tokens.get(i));


      } else {
        goodTuringCount.put(lineArray[i], 1);
        // System.out.println("putting " + i + "  with count " +  tokens.get(i));

      }




      /////////////////
      int index = Arrays.asList(keyArray).indexOf(lineArray[i]);

      if(index != -1) {
        if(tokensCount.containsKey(lineArray[i])) {
          tokensCount.put(lineArray[i], tokensCount.get(lineArray[i]) + 1);
        } else {
          tokensCount.put(lineArray[i], 1);
        }
        if( i + 1 < lineArray.length){
          int index2 = Arrays.asList(keyArray).indexOf(lineArray[i + 1]);
          if(index2 != -1) {
              counter[tokensKey.get(lineArray[i])][tokensKey.get(lineArray[i + 1])] += 1;
//              System.out.println("counter elements");
//              System.out.println("line array i"+tokensKey.get(lineArray[i]));
//              System.out.println("line array i+1" +tokensKey.get(lineArray[i+1]));
//              System.out.println(counter[tokensKey.get(lineArray[i])][tokensKey.get(lineArray[i + 1])]);
          }
        }

      }
    }



  }
  private void addOneSmoothing() {
    for(int i = 0; i < counter.length; i++) {
      for(int j = 0; j < counter[0].length; j++) {
        // System.out.println(" i is " + i + " j is " + j);
        counterAddOneSmoothing[i][j] = counter[i][j] + 1;
      }
    }
  }

  private void goodTuringCalculator(HashMap goodTuringCount){

	Iterator it = goodTuringCount.entrySet().iterator();
    while(it.hasNext()){
      Map.Entry p = (Map.Entry)it.next();
      countN += Integer.parseInt(p.getValue().toString());
      // System.out.println("values is " + p.getValue().toString() + " keys is " + p.getKey().toString());
      String val = p.getValue() + "";

      if(countMap.containsKey(val)) {
        countMap.put(val, countMap.get(val) + 1);
        // System.out.println("putting " + i + "  with count " +  tokens.get(i));


      } else {
        countMap.put(val, 1);
        // System.out.println("putting " + i + "  with count " +  tokens.get(i));

      }

    }

//
//	  Iterator it = goodTuringCount.entrySet().iterator();
//	    while(it.hasNext()){
//	      Map.Entry p = (Map.Entry)it.next();
//	      countN += Integer.parseInt(p.getValue().toString());
//	      // System.out.println("values is " + p.getValue().toString() + " keys is " + p.getKey().toString());
//	      float val = (float) p.getValue();
//
//	      if(countMap.containsKey(val)) {
//	        countMap.put(val, countMap.get(val) + 1);
//	        // System.out.println("putting " + i + "  with count " +  tokens.get(i));
//
//
//	      } else {
//	        countMap.put(val, 1);
//	        // System.out.println("putting " + i + "  with count " +  tokens.get(i));
//
//	      }
//
//	    }





    //System.out.println("counterrrr " + countN );

   //  printGoodTuring(countMap);

   for(int i = 0; i < counter.length; i++) {
      for(int j = 0; j < counter[0].length; j++) {
         // System.out.println(i + " " + j);
         int c = counter[i][j];
         float numerator     = 0;
         float denominator   = 1;

         if(countMap.containsKey(Integer.toString(c)) && countMap.containsKey(Integer.toString(c+1))) {
            numerator     = countMap.get(Integer.toString(c+1));
            denominator   = countMap.get(Integer.toString(c));
         }

         float result = (c + 1) * numerator;
         result /= denominator;
         counterGoodTuring[i][j] = result;

      }
   }
    //arr[][]
    //need an array with count : how many words occur 0 times,1 times,2 times and so on...
    //then apply the formula
    //for the which occur 1 or more times: pOld*c'
    //c' = (c+1)N[c+1]/N[c]
  }

  public void showGoodTuringCount(float a[][]) {
     System.out.println();
     System.out.println("Sentence " + num + " table 5 - COUNTS - Use the bigram model with Good Turing.");
     System.out.println("====================================================================================");
     System.out.println("Sentence: " + sentence);

     System.out.print(String.format("%15s", "") + "");
     for(int i = 0; i < a.length; i++) {
      System.out.print(String.format("%15s", keyArray[i]));
     }
     System.out.println();
     for(int i = 0; i < a.length; i++) {
      // System.out.print(keyArray[i] + "\t\t");
      System.out.print(String.format("%15s", keyArray[i]) + "");
      for(int j =0; j < a[0].length; j++) {
         System.out.print(String.format("%15s", a[i][j]) + "");
      }
      System.out.println();
     }
     System.out.println();
     System.out.println();
 }


   private void printGoodTuring(HashMap countMap){
      Iterator iterator = countMap.keySet().iterator();

      while (iterator.hasNext()) {
        String key = iterator.next().toString();
        String value = countMap.get(key).toString();

      //  System.out.println(key + " " + value);
    }
  }

  private void showAddOne(int[][] a) {
    System.out.println();
    System.out.println("Sentence " + num + " table 3 - COUNTS - Use the bigram model with Add one smoothing.");
    System.out.println("====================================================================================");
    System.out.println("Sentence: " + sentence);

    System.out.print(String.format("%15s", "") + "");
    for(int i = 0; i < a.length; i++) {
      System.out.print(String.format("%15s", keyArray[i]));
    }
    System.out.println();
    for(int i = 0; i < a.length; i++) {
      // System.out.print(keyArray[i] + "\t\t");
      System.out.print(String.format("%15s", keyArray[i]) + "");
      for(int j =0; j < a[0].length; j++) {
        System.out.print(String.format("%15s", a[i][j]) + "");
      }
      System.out.println();
    }
    System.out.println();System.out.println();
  }


  private void show(int[][] a) {
    System.out.println();
    System.out.println("Sentence " + num + " table 1 - COUNTS - Use the bigram model without smoothing.");
    System.out.println("====================================================================================");
    System.out.println("Sentence: " + sentence);

    System.out.print(String.format("%15s", "") + "");
    for(int i = 0; i < a.length; i++) {
      System.out.print(String.format("%15s", keyArray[i]));
    }
    System.out.println();
    for(int i = 0; i < a.length; i++) {
      // System.out.print(keyArray[i] + "\t\t");
      System.out.print(String.format("%15s", keyArray[i]) + "");
      for(int j =0; j < a[0].length; j++) {
        System.out.print(String.format("%15s", a[i][j]) + "");
      }
      System.out.println();
    }
    System.out.println();System.out.println();
  }

  public void showProbability(int[][] a) {
     System.out.println();
     System.out.println("Sentence " + num + " table 2 - PROBABILITY - Use the bigram model without smoothing.");
     System.out.println("====================================================================================");
     System.out.println("Sentence: " + sentence);


     System.out.print(String.format("%15s", "") + "");
     for(int i = 0; i < a.length; i++) {
      System.out.print(String.format("%15s", keyArray[i]));
     }
     System.out.println();
     for(int i = 0; i < a.length; i++) {
      // System.out.print(keyArray[i] + "\t\t");
      float denominator = 0;
      if(tokensCount.containsKey(keyArray[i])) {
         denominator = tokensCount.get(keyArray[i]);
      }


      System.out.print(String.format("%15s", keyArray[i]) + "");
      for(int j =0; j < a[0].length; j++) {
         if(denominator == 0) {
            System.out.print(String.format("%15s", denominator) + "");
         } else {
            System.out.print(String.format("%15s", (float) a[i][j]/ denominator) + "");
         }

      }

      System.out.println();
     }
     System.out.println();System.out.println();
  }

  public void showGoodTuringProbability(float[][] a) {
     System.out.println();
     System.out.println("Sentence " + num + " table 6 - PROBABILITY - Use the bigram model with Good Turing.");
     System.out.println("====================================================================================");
     System.out.println("Sentence: " + sentence);


     System.out.print(String.format("%15s", "") + "");
     for(int i = 0; i < a.length; i++) {
      System.out.print(String.format("%15s", keyArray[i]));
     }
     System.out.println();
     for(int i = 0; i < a.length; i++) {
      // System.out.print(keyArray[i] + "\t\t");
      float denominator = countN;
      System.out.print(String.format("%15s", keyArray[i]) + "");
      for(int j =0; j < a[0].length; j++) {
         if(denominator == 0) {
            System.out.print(String.format("%15s", denominator) + "");
         } else {
            System.out.print(String.format("%15s", (float) a[i][j]/ denominator) + "");
         }

      }
      System.out.println();
     }
     System.out.println();System.out.println();
  }
  private void showAddOneSmoothingProbability(int a[][]) {
    System.out.println();
    System.out.println("Sentence " + num + " table 4 - PROBABILITY - Use the bigram model with Add one smoothing.");
    System.out.println("====================================================================================");
    System.out.println("Sentence: " + sentence);


     System.out.print(String.format("%15s", "") + "");
     for(int i = 0; i < a.length; i++) {
     System.out.print(String.format("%15s", keyArray[i]));
     }
     System.out.println();
     for(int i = 0; i < a.length; i++) {
     // System.out.print(keyArray[i] + "\t\t");
     float denominator = 0;
     if(tokensCount.containsKey(keyArray[i])) {
         denominator = tokensCount.get(keyArray[i]);
     }


     System.out.print(String.format("%15s", keyArray[i]) + "");
     for(int j =0; j < a[0].length; j++) {
         if(denominator == 0) {
            System.out.print(String.format("%15s", denominator) + "");
         } else {
            System.out.print(String.format("%15s", (float)Math.round(((float) a[i][j]/ denominator) * 10000)/10000) + "");
         }

     }
     System.out.println();
     }
     System.out.println();System.out.println();
  }




  private void showBigrams() {
    int i = 1;
    while(i < sentenceArray.length) {
      System.out.print("Probability of " + sentenceArray[i] + " given " + sentenceArray[i - 1] + "is ===");
      System.out.print(counter[tokensKey.get(sentenceArray[i - 1])][tokensKey.get(sentenceArray[i])] + "/" + tokensCount.get(sentenceArray[i - 1]));
      System.out.println();
      i++;
    }

  }

  public void bigramCalculator() {
      getTokens();
      display();
      //showBigrams();
  }

  public double getProbabilityCase1() {
    int i = 1;
    double prob = 1.0;
    while(i < sentenceArray.length) {
      // System.out.println(i - 1);
      // System.out.println("getting " + sentenceArray[i - 1]);
      // System.out.println(tokensCount);
      double denominator = 0;
      if(tokensCount.containsKey(sentenceArray[i - 1])){
        denominator = tokensCount.get(sentenceArray[i - 1]);
      }
      // System.out.println(denominator);

      double numerator   = counter[tokensKey.get(sentenceArray[i - 1])][tokensKey.get(sentenceArray[i])];
      // System.out.println(numerator);
      if(denominator != 0 && numerator != 0 ) {
          prob *= numerator / denominator;
      } else {
         return 0;
      }
      i++;
    }

    // System.out.println("Probability is " + prob);
    return prob;
  }

  public double getProbabilityCase2() {
    int i = 1;
    double prob = 1.0;
    while(i < sentenceArray.length) {
      // System.out.println(i - 1);
      // System.out.println("getting " + sentenceArray[i - 1]);
      // System.out.println(tokensCount);
      double denominator = 0;
      if(tokensCount.containsKey(sentenceArray[i - 1])){
        denominator = tokensCount.get(sentenceArray[i - 1]);
      }
      // System.out.println(denominator);

      double numerator   = counterAddOneSmoothing[tokensKey.get(sentenceArray[i - 1])][tokensKey.get(sentenceArray[i])];


      // System.out.println("numerator : " + numerator + " Denominator : " + denominator + " " + sentenceArray[i - 1]);
      if(denominator == 0) {
            denominator = 1;
      }
      if(denominator != 0 && numerator != 0 ) {
          prob *= numerator / denominator;
      }
      else {
         return 0;
      }
      i++;
    }

    // System.out.println("Probability is " + prob);
    return prob;
  }

  public double getProbabilityCase3() {
    int i = 1;
    double prob = 1.0;
    while(i < sentenceArray.length) {
      // System.out.println(i - 1);
      // System.out.println("getting " + sentenceArray[i - 1]);
      // System.out.println(tokensCount);
      double denominator = countN;
      if(tokensCount.containsKey(sentenceArray[i - 1])){
        denominator = tokensCount.get(sentenceArray[i - 1]);
      }
    //  System.out.println("denominator........");
     // System.out.println(denominator);



      double numerator   = counterGoodTuring[tokensKey.get(sentenceArray[i-1])][tokensKey.get(sentenceArray[i])]+1;
      //double numerator= counterGoodTuring[i-1][i]/denominator;
     // (float) a[i][j]/ denominator
      //double numerator=countMap.get(Integer.toString(i));
      //double numerator   = counterGoodTuring[i-1][i]+1;

     // System.out.println("Numerator........");
     // System.out.println(numerator);
      if(denominator != 0 && numerator != 0 ) {
          prob *= numerator / denominator;
      } else {
         return 0;
      }
      i++;
    }

    // System.out.println("Probability is " + prob);
    return prob;
  }

  public static void main(String[] args) {
    Bigram  s1 = new Bigram (1);
    Bigram  s3= new Bigram (2);
    Bigram  s4= new Bigram (3);
    s3.CreateSentence2("The president has relinquished his control of the company's board.");
    s4.CreateSentence2("The chief executive officer said the last year revenue was good.");
    s1.CreateSentence();

    s1.bigramCalculator();



    double s1prob= s1.getProbabilityCase1();
    System.out.println("");
    System.out.println("Case 1:");
    System.out.println("probability in case 1 is : " + s1prob);

    s1prob= s1.getProbabilityCase2();
    System.out.println("");
    System.out.println("Case 2:");
    System.out.println("probability in case 2 is : " + s1prob);

    s1prob= s1.getProbabilityCase3();
    System.out.println("");
    System.out.println("Case 3:");
    System.out.println("probability in case 3 is : " + s1prob);

    System.out.println("/////////////////////////////");
    System.out.println("/////////////////////////////");

    System.out.println("Table for string 1 an string 2");

    System.out.println("/////////////////////////////");
    System.out.println("/////////////////////////////");


    s3.bigramCalculator();
    s4.bigramCalculator();


    double s3prob = s3.getProbabilityCase1();
    double s4prob = s4.getProbabilityCase1();
    System.out.println("");
    System.out.println("Case 1:");
    System.out.println("probability of 1 is : " + s3prob);
    System.out.println("probability of 2 is : " + s4prob);

    if(s3prob > s3prob) {
      System.out.println("Probability of sentence 1 is more - " + s3prob + "%");
    } else {
      System.out.println("Probability of sentence 2 is more - " + s4prob + "%");
    }

    System.out.println("");
    s3prob = s3.getProbabilityCase2();
    s4prob = s4.getProbabilityCase2();
    System.out.println("Case 2:");
    System.out.println("probability of 1 is : " + s3prob);
    System.out.println("probability of 2 is : " + s4prob);

    if(s3prob > s4prob) {
      System.out.println("Probability of sentence 1 is more - " + s4prob + "%");
    } else {
      System.out.println("Probability of sentence 2 is more - " + s4prob + "%");
    }

    System.out.println("");
    s3prob = s3.getProbabilityCase3();
    s4prob = s4.getProbabilityCase3();
    System.out.println("Case 3:");
    System.out.println("probability of 1 is : " + s3prob);
    System.out.println("probability of 2 is : " + s4prob);

    if(s3prob > s4prob) {
      System.out.println("Probability of sentence 1 is more - " + s3prob + "%");
    } else {
      System.out.println("Probability of sentence 2 is more - " + s4prob + "%");
    }
    System.out.println("");
    System.out.println("");

  }

}
