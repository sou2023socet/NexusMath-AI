package mathapp.numberproperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberProperties {
	
	private static boolean isPerfectNumber;
	private static boolean isSemiPrimeNumber;

    // positive number
    public static boolean positive(double number) {
        return number >= 0;
    }
    
    // interger number
    public static boolean integer(double number) {
    	return number == Math.floor(number);
    }
    
    // irrational number
	//    irrational(2)   // true  → √2 is irrational
	//    irrational(3)   // true  → √3 is irrational
	//    irrational(4)   // false → √4 = 2
	//    irrational(9)   // false → √9 = 3

    public static boolean irrational(double number) {
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            return false;
        }
        if (negative(number)) {
            return false;
        }
        double sqrt = Math.sqrt(number);
        return sqrt != Math.floor(sqrt);
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
    
 // interger Number (all >= 0)
    public static boolean isIntergerNumberList(ArrayList<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!(integer(number))) return false;
        }
        return true;
    }
    
    // List of positive number check and identify
    public static boolean isPositiveNumberList(ArrayList<Double> numberSeries) {
        if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!(positive(number))) return false;
        }
        return true;
    }
    
    // List of Negative number check and identify
    public static boolean isNegativeNumberList(ArrayList<Double> numberSeries) {
        return !isPositiveNumberList(numberSeries);
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
    
    // irreantional number
    public static boolean isIrrationalNumberList(ArrayList<Double> numberSeries) {
    	if (numberSeries == null || numberSeries.isEmpty()) return false;
        for (double number : numberSeries) {
            if (!irrational(number)) return false;
        }
        return true;
    }
    
    // real number
    // check number list comes under real number (non-empty)
    public static boolean isRealNumberList(ArrayList<Double> numberSeries) {
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

	// =========================================================
	// 3. REAL NUMBER
	// =========================================================

	/*
	 * Every finite double value is a real number.
	 *
	 * Examples:
	 *
	 * 10 -> true -10.5 -> true 0 -> true NaN -> false Infinity -> false
	 */
	public static boolean real(double number) {

		if (Double.isNaN(number) || Double.isInfinite(number)) {
			return false;
		}

		return true;
	}

	// =========================================================
	// 4. COMPLEX NUMBER
	// =========================================================

	/*
	 * Complex number:
	 *
	 * a + bi
	 *
	 * a = real part b = imaginary part
	 *
	 * A number is genuinely non-real complex when b != 0.
	 *
	 * complex(5, 3) -> true -> 5 + 3i complex(5, 0) -> false -> 5 is real
	 * complex(0, 3) -> true -> 3i
	 */
	public static boolean complex(double realPart, double imaginaryPart) {

		if (Double.isNaN(realPart) || Double.isInfinite(realPart) || Double.isNaN(imaginaryPart)
				|| Double.isInfinite(imaginaryPart)) {

			return false;
		}

		return imaginaryPart != 0;
	}

	// =========================================================
	// 5. NUMBER SETS & CLASSIFICATION
	// =========================================================

	/*
	 * Classification hierarchy:
	 *
	 * Natural ⊂ Whole ⊂ Integer ⊂ Rational ⊂ Real ⊂ Complex
	 *
	 * For a finite double:
	 *
	 * 5 -> Natural -> Whole -> Integer -> Rational -> Real -> Complex (as 5 + 0i)
	 *
	 * -5 -> Integer -> Rational -> Real -> Complex
	 *
	 * 5.5 -> Rational -> Real -> Complex
	 *
	 * IMPORTANT: Every finite decimal/double value is rational as a mathematical
	 * value because a finite decimal can be represented as p/q.
	 *
	 * However, this does NOT mean double can exactly store π or √2.
	 */
	public static List<String> classifyNumber(double number) {

		List<String> result = new ArrayList<>();

		if (Double.isNaN(number) || Double.isInfinite(number)) {
			return result;
		}

		result.add("Complex");

		result.add("Real");

		// Every finite double value is a rational mathematical value.
		result.add("Rational");

		if (number == Math.floor(number)) {

			result.add("Integer");

			if (number >= 0) {
				result.add("Whole");
			}

			if (number > 0) {
				result.add("Natural");
			}
		}

		return result;
	}

	// =========================================================
	// 6. NUMBER LINE
	// =========================================================

	/*
	 * Position relative to zero.
	 *
	 * -5 -> Left 0 -> At zero 5 -> Right
	 */
	public static String numberLine(double number) {

		if (Double.isNaN(number) || Double.isInfinite(number)) {

			return "❌ Not a real number";
		}

		if (number < 0) {
			return "⬅ Left of 0";
		}

		if (number > 0) {
			return "Right of 0 ➡";
		}

		return "📍 At 0";
	}

	// =========================================================
	// 7. PLACE VALUE
	// =========================================================

	/*
	 * Example:
	 *
	 * 5824
	 *
	 * 5 -> Thousands -> 5000 8 -> Hundreds -> 800 2 -> Tens -> 20 4 -> Ones -> 4
	 *
	 * Decimal:
	 *
	 * 12.34
	 *
	 * 1 -> Tens 2 -> Ones 3 -> Tenths 4 -> Hundredths
	 *
	 * This method returns a textual place-value breakdown.
	 */
	public static String placeValue(double number) {

		if (Double.isNaN(number) || Double.isInfinite(number)) {

			return "❌ Invalid number";
		}

		String text = BigDecimal.valueOf(number).toPlainString();

		boolean negative = text.startsWith("-");

		if (negative) {
			text = text.substring(1);
		}

		String[] parts = text.split("\\.", -1);

		String whole = parts[0];
		String decimal = parts.length > 1 ? parts[1] : "";

		StringBuilder result = new StringBuilder();

		if (negative) {
			result.append("Sign: Negative (-)\n\n");
		}

		// Whole-number part
		String[] wholePlaces = { "Ones", "Tens", "Hundreds", "Thousands", "Ten-Thousands", "Hundred-Thousands",
				"Millions", "Ten-Millions", "Hundred-Millions", "Billions", "Ten-Billions", "Hundred-Billions",
				"Trillions" };

		for (int i = 0; i < whole.length(); i++) {

			int digit = whole.charAt(i) - '0';

			int position = whole.length() - 1 - i;

			String place;

			if (position < wholePlaces.length) {
				place = wholePlaces[position];
			} else {
				place = "10^" + position;
			}

			if (result.length() > 0) {
				result.append("\n");
			}

			result.append(digit).append(" → ").append(place);
		}

		// Decimal part
		String[] decimalPlaces = { "Tenths", "Hundredths", "Thousandths", "Ten-Thousandths", "Hundred-Thousandths",
				"Millionths", "Ten-Millionths", "Hundred-Millionths" };

		if (!decimal.isEmpty()) {

			result.append("\n\nDecimal places:");

			for (int i = 0; i < decimal.length(); i++) {

				int digit = decimal.charAt(i) - '0';

				String place;

				if (i < decimalPlaces.length) {
					place = decimalPlaces[i];
				} else {
					place = "10^-" + (i + 1);
				}

				result.append("\n").append(digit).append(" → ").append(place);
			}
		}

		return result.toString();
	}

	// =========================================================
	// 8. COMPARING TWO NUMBERS
	// =========================================================

	/*
	 * compareNumbers(10, 20) -> -1
	 *
	 * compareNumbers(20, 10) -> 1
	 *
	 * compareNumbers(10, 10) -> 0
	 *
	 * Same convention as Comparator:
	 *
	 * negative -> first < second zero -> first = second positive -> first > second
	 */
	public static int compareNumbers(double first, double second) {

		if (Double.isNaN(first) || Double.isNaN(second) || Double.isInfinite(first) || Double.isInfinite(second)) {

			throw new IllegalArgumentException("Only finite real numbers can be compared.");
		}

		return Double.compare(first, second);
	}

	// =========================================================
	// 9. ORDERING - ASCENDING // change with sorting algorithms
	// =========================================================

	/*
	 * Example:
	 *
	 * [5, 2, 9, 1]
	 *
	 * ->
	 *
	 * [1, 2, 5, 9]
	 */
	public static ArrayList<Double> orderAscending(List<Double> numbers) {

		ArrayList<Double> result = new ArrayList<>();

		if (numbers == null) {
			return result;
		}

		for (Double number : numbers) {

			if (number == null || Double.isNaN(number) || Double.isInfinite(number)) {

				throw new IllegalArgumentException("Ordering requires finite real numbers.");
			}

			result.add(number);
		}

		result.sort(Double::compare);

		return result;
	}

	// =========================================================
	// 10. ORDERING - DESCENDING
	// =========================================================

	/*
	 * Example:
	 *
	 * [5, 2, 9, 1]
	 *
	 * ->
	 *
	 * [9, 5, 2, 1]
	 */
	public static ArrayList<Double> orderDescending(List<Double> numbers) {

		ArrayList<Double> result = new ArrayList<>();

		if (numbers == null) {
			return result;
		}

		for (Double number : numbers) {

			if (number == null || Double.isNaN(number) || Double.isInfinite(number)) {

				throw new IllegalArgumentException("Ordering requires finite real numbers.");
			}

			result.add(number);
		}

		result.sort((a, b) -> Double.compare(b, a));

		return result;
	}

	// =========================================================
	// 11. NUMERICAL DISTANCE
	// =========================================================

	/*
	 * Distance between two numbers:
	 *
	 * |a - b|
	 *
	 * distance(10, 4) -> 6 distance(4, 10) -> 6 distance(-5, 5) -> 10
	 */
	public static double numericalDistance(double first, double second) {

		if (Double.isNaN(first) || Double.isNaN(second) || Double.isInfinite(first) || Double.isInfinite(second)) {

			throw new IllegalArgumentException("Distance requires finite real numbers.");
		}

		return Math.abs(first - second);
	}

	// =========================================================
	// 12. PROPERTIES OF NUMBERS
	// =========================================================

	/*
	 * Returns the fundamental properties applicable to a number.
	 */
	/*
	 * ALL NUMBER PROPERTY WILL BE USE FOR CHECK AND COMPARE L.H.S & R.H.S
	 * check Commutative Property
	 * Associative Property
	 * Distributive Property
	 * Identity Property
	 * */
	public static boolean commutative() {
		return false;
	}
	public static List<String> propertiesOfNumber(double number) {

		List<String> result = new ArrayList<>();

		if (Double.isNaN(number) || Double.isInfinite(number)) {

			return result;
		}

		result.add("Real");

		result.add("Rational");

		if (number == Math.floor(number)) {

			result.add("Integer");

			if (number >= 0) {
				result.add("Whole");
			}

			if (number > 0) {
				result.add("Natural");
			}

			if (number != 0) {
				result.add("Signed Integer");
			}
		}

		if (number == 0) {
			result.add("Zero");
		}

		if (number > 0) {
			result.add("Positive");
		}

		if (number < 0) {
			result.add("Negative");
		}

		if (number != 0) {

			if (number == Math.floor(number)) {

				if (number % 2 == 0) {
					result.add("Even");
				} else {
					result.add("Odd");
				}
			}
		}

		return result;
	}

	// =========================================================
	// 13. CLOSURE PROPERTY
	// =========================================================

	/*
	 * Closure means:
	 *
	 * For every a,b in the set:
	 *
	 * a operation b
	 *
	 * must also belong to the set.
	 *
	 * Supported operations:
	 *
	 * + Addition - Subtraction * Multiplication / Division
	 *
	 * IMPORTANT:
	 *
	 * Because a finite List is being used as the "set", this checks closure of the
	 * supplied finite set.
	 *
	 * Example:
	 *
	 * Set = [1,2,3] Addition: 1+1=2 -> in set 1+2=3 -> in set 1+3=4 -> not in set
	 *
	 * Therefore -> false.
	 */
	public static boolean closureProperty(List<Double> numbers, String operation) {

		if (numbers == null || numbers.isEmpty() || operation == null) {

			return false;
		}

		for (Double a : numbers) {

			if (a == null || Double.isNaN(a) || Double.isInfinite(a)) {

				return false;
			}

			for (Double b : numbers) {

				if (b == null || Double.isNaN(b) || Double.isInfinite(b)) {

					return false;
				}

				double result;

				switch (operation.trim()) {

				case "+":

					result = a + b;
					break;

				case "-":

					result = a - b;
					break;

				case "*":

					result = a * b;
					break;

				case "/":

					if (b == 0) {
						return false;
					}

					result = a / b;
					break;

				default:

					throw new IllegalArgumentException("Supported operations: +, -, *, /");
				}

				boolean found = false;

				for (Double value : numbers) {

					if (value != null && Double.compare(value, result) == 0) {

						found = true;
						break;
					}
				}

				if (!found) {
					return false;
				}
			}
		}

		return true;
	}

	// =========================================================
	// 14. COMMUTATIVE PROPERTY
	// =========================================================

	/*
	 * Commutative:
	 *
	 * a + b = b + a a × b = b × a
	 *
	 * Subtraction and division are NOT commutative.
	 *
	 * The method tests every pair in the supplied list.
	 */
	public static boolean commutativeProperty(List<Double> numbers, String operation) {

		if (numbers == null || numbers.isEmpty() || operation == null) {

			return false;
		}

		if (!operation.equals("+") && !operation.equals("*")) {

			return false;
		}

		for (Double a : numbers) {

			if (a == null || Double.isNaN(a) || Double.isInfinite(a)) {

				return false;
			}

			for (Double b : numbers) {

				if (b == null || Double.isNaN(b) || Double.isInfinite(b)) {

					return false;
				}

				double first;
				double second;

				if (operation.equals("+")) {

					first = a + b;
					second = b + a;

				} else {

					first = a * b;
					second = b * a;
				}

				if (Double.compare(first, second) != 0) {
					return false;
				}
			}
		}

		return true;
	}

	// =========================================================
	// 15. ASSOCIATIVE PROPERTY
	// =========================================================

	/*
	 * Associative:
	 *
	 * (a + b) + c = a + (b + c)
	 *
	 * (a × b) × c = a × (b × c)
	 *
	 * Addition and multiplication are associative.
	 *
	 * Subtraction and division are NOT associative.
	 *
	 * All triples from the supplied list are tested.
	 */
	public static boolean associativeProperty(List<Double> numbers, String operation) {

		if (numbers == null || numbers.size() < 3 || operation == null) {

			return false;
		}

		if (!operation.equals("+") && !operation.equals("*")) {

			return false;
		}

		for (Double a : numbers) {

			if (a == null || Double.isNaN(a) || Double.isInfinite(a)) {

				return false;
			}

			for (Double b : numbers) {

				if (b == null || Double.isNaN(b) || Double.isInfinite(b)) {

					return false;
				}

				for (Double c : numbers) {

					if (c == null || Double.isNaN(c) || Double.isInfinite(c)) {

						return false;
					}

					double left;
					double right;

					if (operation.equals("+")) {

						left = (a + b) + c;
						right = a + (b + c);

					} else {

						left = (a * b) * c;
						right = a * (b * c);
					}

					/*
					 * Math operations using double can have floating-point rounding differences.
					 *
					 * Use a tolerance rather than exact equality.
					 */
					double tolerance = 1e-12 * Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));

					if (Math.abs(left - right) > tolerance) {
						return false;
					}
				}
			}
		}

		return true;
	}

	// =========================================================
	// 16. DISTRIBUTIVE PROPERTY
	// =========================================================

	/*
	 * Distributive:
	 *
	 * a × (b + c) = (a × b) + (a × c)
	 *
	 * and
	 *
	 * a × (b - c) = (a × b) - (a × c)
	 *
	 * The operation being tested is multiplication over addition or subtraction.
	 */
	public static boolean distributiveProperty(List<Double> numbers, String operation) {

		if (numbers == null || numbers.size() < 3 || operation == null) {

			return false;
		}

		if (!operation.equals("+") && !operation.equals("-")) {

			return false;
		}

		for (Double a : numbers) {

			if (a == null || Double.isNaN(a) || Double.isInfinite(a)) {

				return false;
			}

			for (Double b : numbers) {

				if (b == null || Double.isNaN(b) || Double.isInfinite(b)) {

					return false;
				}

				for (Double c : numbers) {

					if (c == null || Double.isNaN(c) || Double.isInfinite(c)) {

						return false;
					}

					double left;
					double right;

					if (operation.equals("+")) {

						left = a * (b + c);
						right = (a * b) + (a * c);

					} else {

						left = a * (b - c);
						right = (a * b) - (a * c);
					}

					double tolerance = 1e-12 * Math.max(1.0, Math.max(Math.abs(left), Math.abs(right)));

					if (Math.abs(left - right) > tolerance) {
						return false;
					}
				}
			}
		}

		return true;
	}

	// =========================================================
	// 17. IDENTITY ELEMENT
	// =========================================================

	/*
	 * Identity:
	 *
	 * Addition:
	 *
	 * a + 0 = a
	 *
	 * Identity = 0
	 *
	 * Multiplication:
	 *
	 * a × 1 = a
	 *
	 * Identity = 1
	 *
	 * The method checks whether the requested identity belongs to the supplied set
	 * and behaves as an identity for every element.
	 */
	public static boolean identityElement(List<Double> numbers, String operation) {

		if (numbers == null || numbers.isEmpty() || operation == null) {

			return false;
		}

		double identity;

		if (operation.equals("+")) {
			identity = 0.0;
		} else if (operation.equals("*")) {
			identity = 1.0;
		} else {
			return false;
		}

		boolean identityExists = false;

		for (Double value : numbers) {

			if (value != null && Double.compare(value, identity) == 0) {

				identityExists = true;
				break;
			}
		}

		if (!identityExists) {
			return false;
		}

		for (Double value : numbers) {

			if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {

				return false;
			}

			double result;

			if (operation.equals("+")) {
				result = value + identity;
			} else {
				result = value * identity;
			}

			if (Double.compare(result, value) != 0) {
				return false;
			}
		}

		return true;
	}

	// =========================================================
	// 18. INVERSE ELEMENT
	// =========================================================

	/*
	 * Additive inverse:
	 *
	 * a + (-a) = 0
	 *
	 * Multiplicative inverse:
	 *
	 * a × (1/a) = 1
	 *
	 * For multiplication:
	 *
	 * 0 has NO multiplicative inverse.
	 *
	 * The method checks whether every element in the supplied set has its required
	 * inverse inside that same set.
	 */
	public static boolean inverseElement(List<Double> numbers, String operation) {

		if (numbers == null || numbers.isEmpty() || operation == null) {

			return false;
		}

		if (!operation.equals("+") && !operation.equals("*")) {

			return false;
		}

		for (Double value : numbers) {

			if (value == null || Double.isNaN(value) || Double.isInfinite(value)) {

				return false;
			}

			double inverse;

			if (operation.equals("+")) {

				inverse = -value;

			} else {

				if (value == 0) {
					return false;
				}

				inverse = 1.0 / value;
			}

			boolean found = false;

			for (Double candidate : numbers) {

				if (candidate != null) {

					double tolerance = 1e-12 * Math.max(1.0, Math.max(Math.abs(candidate), Math.abs(inverse)));

					if (Math.abs(candidate - inverse) <= tolerance) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				return false;
			}
		}

		return true;
	}
}

