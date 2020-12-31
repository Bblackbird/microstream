package com.bblackbird;

public class Dukson {


    public static void main(String[] args) {

        //int number = Integer.parseInt(args[0]);

        String[] argz = {"17.0", "23.0", "5.0", "1.1", "6.9", "0.3"};

        double smallest = Double.MAX_VALUE;
        double secondSmallest = Double.MAX_VALUE;

        for(String arg : argz) {
            double value = Double.parseDouble(arg);

            if(value < smallest) {
                secondSmallest = smallest;
                smallest = value;
            } else if(value == smallest) { // if equal
                secondSmallest = value;
            } else if(value < secondSmallest) {// i
                secondSmallest = value;
            }
        }

        System.out.println(smallest);
        System.out.println(secondSmallest);

    }

}
