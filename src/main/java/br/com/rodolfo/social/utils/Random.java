package br.com.rodolfo.social.utils;

public class Random {
    private final String[] lowerCaseLetters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private final String[] upperCaseLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private final String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private final String[] all;

    public Random() {
        this.all = new String[upperCaseLetters.length + lowerCaseLetters.length + numbers.length];
        for (int i = 0; i < upperCaseLetters.length; i++)
            all[i] = upperCaseLetters[i];
        for (int i = 0; i < lowerCaseLetters.length; i++)
            all[i + upperCaseLetters.length] = lowerCaseLetters[i];
        for (int i = 0; i < numbers.length; i++)
            all[i + upperCaseLetters.length + lowerCaseLetters.length] = numbers[i];
    }

    public String string(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * (all.length));
            sb.append(all[index]);
        }
        return sb.toString();
    }

    public String lowerCaseLetters(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * (lowerCaseLetters.length));
            sb.append(lowerCaseLetters[index]);
        }
        return sb.toString();
    }

    public String upperCaseLetters(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * (upperCaseLetters.length));
            sb.append(upperCaseLetters[index]);
        }
        return sb.toString();
    }

    public String numbers(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * (numbers.length));
            sb.append(numbers[index]);
        }
        return sb.toString();
    }
}
