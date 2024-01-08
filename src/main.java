import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;
public class main {
    public static Random random;
    public static Scanner scanner = new Scanner(System.in);
    public static int score;
    public static int difficulty = 0;
    public static int input;
    public static int alive;
    public static String[] dictionaryFile;

    static {
        try {
            dictionaryFile = new String[countLinesInFile("dictionary")];
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static int newWordTime = 3000;
    public static String strInput;
    public static String username;
    public static int linesOfWords = 30;
    public static String[] wordsDisplay = new String[linesOfWords];
    public static String[] wordsTemp = new String[linesOfWords];
    public static void main(String[] args) throws IOException, InterruptedException {
        dictionaryFile = fileIntoArray("dictionary");
        for(int i = 0; i<wordsDisplay.length; i++){
            wordsDisplay[i]="";
            wordsTemp[i]="";
        }
        askUsername();
        displayMenu();
        userSelectMenu();
    }
    public static void gameOver() throws IOException, InterruptedException {
        System.out.println("GAME OVER!");
        updateLeaderboard("leaderboard", username, score);
        tryAgainPrompt();
    }
    public static void tryAgainPrompt() throws IOException, InterruptedException {
        System.out.println("Try Again? (y/n, please type twice to confirm.)");
        Scanner tryAgain = new Scanner(System.in);
        strInput = tryAgain.nextLine();
        if(strInput.equals("y")){
            startGame();
        }
        if(strInput.equals("n")){
            System.out.println("Goodbye!");
            System.exit(1);
        }
        else {
            invalid();
        }
        tryAgain.close();
    }
    public static String randomFromFile(String fileName) throws FileNotFoundException {
        String[] word = fileIntoArray(fileName);
        Random rand = new Random();
        int totalLines = word.length;
        int randomLineInFile;
        if (rand.nextInt(difficulty + 2) == 1) {
            return "";
        }
        else {
            if ((1000 * difficulty * difficulty * difficulty) >= totalLines) {
                randomLineInFile = rand.nextInt(totalLines - 170000) + 170000;
            }
            else {
                randomLineInFile = rand.nextInt(1000 * difficulty * difficulty * difficulty) + (100 * difficulty * difficulty * difficulty);
            }
        }
        return word[randomLineInFile];
    }
    public static void startGame() throws IOException, InterruptedException { //change this if find a way to clear all output on the screen
        for(int i = 0; i<=50; i++){
            System.out.println("");
        }
        System.out.print("Loading... please wait.");
        displayFile("words");
        difficulty=0;
        alive=1;
        score=0;
        newWordTime=3000;
        for (int i = 0; i<wordsDisplay.length; i++){
            wordsDisplay[i]="";
            wordsTemp[i]="";
        }
        Thread userInputThread = new Thread(() -> {
            while (alive!=0) {
                try {
                    checkUserInput();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread difficultyIncreaseThread = new Thread(() -> {
            while (alive!=0) {
                difficulty++;
                newWordTime -= 50;
                try {
                    Thread.sleep(20000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        userInputThread.start();
        difficultyIncreaseThread.start();
        while(alive!=0){
            Thread.sleep(newWordTime);
            generateWord(randomFromFile("dictionary"));
            updateStats();
        }
    }
    public static void checkUserInput() throws IOException {
        strInput = scanner.nextLine();
        for (int i = 0; i < wordsDisplay.length; i++) {
            if (wordsDisplay[i].trim().equals(strInput)) {
                score = score + (wordsDisplay[i].trim().length() * 100);
                wordsDisplay[i] = "";
                wordsTemp[i] = "";
            }
        }
    }
    public static void updateStats() throws FileNotFoundException {
        int totalLines = countLinesInFile("dictionary");
        System.out.print("\rDifficulty: " + difficulty + " (Min length: " + dictionaryFile[100 * difficulty * difficulty * difficulty].length() + " Max length: ");
        if ((1000 * difficulty * difficulty * difficulty) >= totalLines) {
            System.out.print(15 + ")");
        }
        else{
            System.out.print(dictionaryFile[1000 * difficulty * difficulty * difficulty].length() + ")");
        }
        System.out.println("");
        System.out.print("Score: " + score);
        System.out.println("");
    }
    public static void generateWord(String word) throws IOException, InterruptedException {
        if(word == ""){
            updateWordsArray(word);
        }
        else{
            String temp = "";
            Random rand = new Random();
            for (int i = 0; i < rand.nextInt(100); i++){
                word = " " + word;
            }
            updateWordsArray(word);
        }
        for(int i = wordsDisplay.length-1; i>=0; i--){
            System.out.println(wordsDisplay[i]);
        }
    }
    public static void updateWordsArray(String word) throws IOException, InterruptedException {
        for(int i = wordsDisplay.length-2; i>=0; i--){
            wordsDisplay[i]=wordsTemp[i+1];
        }
        wordsTemp[27]=word;
        wordsDisplay[27]=word;
        for(int i = 0; i<wordsDisplay.length; i++){
            wordsTemp[i]=wordsDisplay[i];
        }
        if(wordsDisplay[0]!=""){
            alive = 0;
            gameOver();
        }
    }
    public static void userSelectMenu() throws IOException, InterruptedException { //prompts user to select one from menu
        System.out.println("Enter a number: ");
        input = scanner.nextInt();
        if(input == 1){
           startGame();
        }
        else if(input == 2){
            displayFile("leaderboard");
            userSelectMenu();
        }
        else if(input == 3){
            System.out.println("Goodbye!");
            System.exit(0);
        }
        else{
            invalid();
            userSelectMenu();
        }
    }
    public static void displayFile(String fileName) throws FileNotFoundException { //prints out content of a file
        File file = new File(fileName);
        Scanner fileScanner = new Scanner(file);
        while (fileScanner.hasNextLine()) {
            System.out.println(fileScanner.nextLine());
        }
        fileScanner.close();
    }
    public static void invalid(){
        System.out.println("Invalid Input!");
    } //error message
    public static void askUsername(){ //ask for username (used at start of program)
        System.out.println("Fullscreen is recommended for this game!");
        System.out.println("What's your username?");
        username = scanner.nextLine();
        System.out.println("Welcome, " + username + ".");
    }
    public static void displayMenu() { //displays main menu UI
        System.out.println("                         ▄▄▌ ▐ ▄▌      ▄▄▄  ·▄▄▄▄    ▄▄▄▄· ▄▄▌  ▪  ▄▄▄▄▄·▄▄▄▄•");
        System.out.println("                         ██· █▌▐█ ▄█▀▄ ▀▄ █·██· ██   ▐█ ▀█▪██•  ██ •██  ▪▀·.█▌");
        System.out.println("                         ██▪▐█▐▐▌▐█▌.▐▌▐▀▀▄ ▐█▪ ▐█▌  ▐█▀▀█▄██ ▪ ▐█· ▐█.▪▄█▀▀▀•");
        System.out.println("                         ▐█▌██▐█▌▐█▌.▐▌▐█•█▌██. ██   ██▄▪▐█▐█▌ ▄▐█▌ ▐█▌·█▌▪▄█▀");
        System.out.println("                          ▀▀▀▀ ▀▪ ▀█▄▀▪.▀  ▀▀▀▀▀▀•   ·▀▀▀▀ .▀▀▀ ▀▀▀ ▀▀▀ ·▀▀▀ •");
        System.out.println("\n                                            Type 1 to play");
        System.out.println("                                      Type 2 to display leaderboard");
        System.out.println("                                            Type 3 to quit");
    }
    public static void addWord(String outPutFilename, String word, int num) throws IOException {
        File file = new File(outPutFilename);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        bufferedWriter.newLine();
        bufferedWriter.write(word + " " + num);
        bufferedWriter.close();
    }
    public static void updateLeaderboard(String outputFilename, String username, int score) throws IOException {
        File file = new File(outputFilename);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String[] lines = new String[5];
        for (int i = 0; i < 5; i++) {
            lines[i] = bufferedReader.readLine();
        }
        bufferedReader.close();
        boolean scoreUpdated = false;
        for (int i = 0; i < 5; i++) {
            String line = lines[i];
            int existingScore = Integer.parseInt(line.substring(line.lastIndexOf(":") + 1).trim());
            if (score > existingScore) {
                lines[i] = String.format("%d - Username: %s | Score: %d", i + 1, username, score);
                scoreUpdated = true;
                break;
            }
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        if (!scoreUpdated) {
            bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(String.format("%d - Username: %s | Score: %d", 6, username, score));
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
    }
    public static void addToFile(String outPutFilename, String word) throws IOException { //add a word to a file
        File file = new File(outPutFilename);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        bufferedWriter.newLine();
        bufferedWriter.write(word);
        bufferedWriter.close();
    }
    private static String[] fileIntoArray(String inputFilename) throws FileNotFoundException {
        File file = new File(inputFilename);
        Scanner scanner = new Scanner(file);
        int numberOfLinesInFile = countLinesInFile(inputFilename);
        String[] data = new String[numberOfLinesInFile];
        int index = 0;
        while (scanner.hasNextLine()) {
            data[index++] = scanner.nextLine();
        }
        scanner.close();
        return data;
    }
    private static int countLinesInFile(String inputFilename) throws FileNotFoundException {
        File file = new File(inputFilename);
        Scanner scanner = new Scanner(file);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            lineCount++;
            scanner.nextLine();
        }
        scanner.close();
        return lineCount;
    }
}