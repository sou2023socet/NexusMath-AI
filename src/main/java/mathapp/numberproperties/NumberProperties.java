package mathapp.numberproperties;

import java.util.ArrayList;
import java.util.List;

public class NumberProperties {
	
	private static boolean isPerfectNumber;
	private static boolean isSemiPrimeNumber;

    // positive number
    public static boolean positive(double number) {
        return number >= 0;
    }

    // negative number
    public static boolean negative(double number) {
        return number < 0;
    }

    // prime number
    public static boolean prime(int number) {
        if (number < 2) return false;
        if (number % 2 == 0) return number == 2;
        for (int i = 3; (long) i * i <= number; i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }


    // CoPrime Number
    public static boolean coprime(int number) {
        return false;
    }

    // odd number
    public static boolean oddNumber(double n) {
        return ((long) n) % 2 != 0;
    }

    // even number
    public static boolean evenNumber(double n) {
        return ((long) n) % 2 == 0;
    }

    // check number list comes under real number (non-empty)
    public static boolean isRealNumberList(ArrayList<Integer> numberSeries) {
        return numberSeries != null && !numberSeries.isEmpty();
    }

    // Natural Number (all > 0)
    public static boolean isNaturalNumberList(ArrayList<Integer> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (int num : numberSeries) {
            if (num <= 0) return false;
        }
        return true;
    }

    // whole Number (all >= 0)
    public static boolean isWholeNumberList(ArrayList<Integer> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (int number : numberSeries) {
            if (number < 0) return false;
        }
        return true;
    }
    
    public static double absoluteNumber(double Number) {
    	return (Number * (-1));
    }
    
    public static boolean isPostiviNumber(double Number) {
    	if (Number >= 0) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isNegativeNumber(double Number) {
    	return (!(isPostiviNumber(Number)));
    }
    
    // ArmStrong Number
    public static boolean isArmStrongNumber(double Number) {
    	if (isPostiviNumber(Number)) {
    		int originalNumber = (int)Number;
            int sum = 0;
            
            int digits = String.valueOf(Number).length();
            
            while (Number > 0) {
                double remainder = Number % 10;
                sum += Math.pow(remainder, digits);
                Number = Number / 10;
            }
            return sum == originalNumber;
    	}
    	return false;
    }
    
    

    // rational number (non-empty)
    public static boolean isRationalNumberList(ArrayList<Double> numberSeries) {
        return numberSeries != null && !numberSeries.isEmpty();
    }

    // check any one even number
    public static boolean anyEvenInList(ArrayList<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (evenNumber(number)) return true;
        }
        return false;
    }

    // odd number list
    public static boolean anyOddInList(ArrayList<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (oddNumber(number)) return true;
        }
        return false;
    }

    // odd count in list
    public static int oddCountInList(ArrayList<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return 0;
        int count = 0;
        for (double number : numberSeries) {
            if (oddNumber(number)) count++;
        }
        return count;
    }

    // even count in list
    public static int evenCountInList(ArrayList<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return 0;
        int count = 0;
        for (double number : numberSeries) {
            if (evenNumber(number)) count++;
        }
        return count;
    }

    // factors (prime factors style was not clear; keeping same behavior: add i where n % i == 0, for i in [2,n))
    public static List<Integer> factors(double numberFactor, List<Integer> numberFactorList) {
        int n = (int) numberFactor;
        int number = 0; // perfectnumber check 
        for (int i = 1; i < n; i++) {
            if (n % i == 0) {
            	number += i;
                numberFactorList.add(i);
            }
        }
        if (number == n) {
        	setIsPerfectNumber(true);
        }else {
        	setIsPerfectNumber(false);
        }
        return numberFactorList;
    }
    
    public static void setIsPerfectNumber(boolean isPerfectNumber) {
    	NumberProperties.isPerfectNumber = isPerfectNumber;
    }
    
    public static boolean getIsPerfectNumber() {
    	return isPerfectNumber;
    }
    
    public static void setIsSemiPrimeNumber(boolean isSemiPrimeNumber) {
    	NumberProperties.isSemiPrimeNumber = isSemiPrimeNumber;
    }
    
    public static boolean setIsSemiPrimeNumber() {
    	return isSemiPrimeNumber;
    }
    // coprime number
    public static List<Integer> coPrimeFactor(double numberFactor,List<Integer> primeNumberFactorList){
    	boolean semiPrime = true;
        if (numberFactor < 2){
            primeNumberFactorList.add(1);
            return primeNumberFactorList;
        }else{
            int minimumnumber;
            minimumnumber =  evenNumber(numberFactor) ? 2 : 3;
            while (numberFactor > 1) {
                if(numberFactor%minimumnumber == 0){
                    numberFactor /= minimumnumber;
                    primeNumberFactorList.add(minimumnumber);
                    if (!prime(minimumnumber) && semiPrime) {
                    	semiPrime = false;
                    }
                }else{
                    minimumnumber += 1;
                }
                
            }
            if (semiPrime) {
            	setIsSemiPrimeNumber(true);
            }else {
            	setIsSemiPrimeNumber(false);
            }
        }
        return primeNumberFactorList;
    }
    
    private static int findHcf(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // hcf of list
    public static int hcf(ArrayList<Double> numberList) {
        if (numberList == null || numberList.isEmpty()) return 0;
        int result = numberList.get(0).intValue();
        for (int i = 1; i < numberList.size(); i++) {
            result = findHcf(result, numberList.get(i).intValue());
            if (result == 1) return 1;
        }
        return result;
    }

    // lcm of list
    public static int lcm(ArrayList<Double> numberList) {
        if (numberList == null || numberList.isEmpty()) return 0;
        int result = numberList.get(0).intValue();
        for (int i = 1; i < numberList.size(); i++) {
            int nextNum = numberList.get(i).intValue();
            if (nextNum == 0 || result == 0) return 0;
            result = (result * nextNum) / findHcf(result, nextNum);
        }
        return result;
    }
}

