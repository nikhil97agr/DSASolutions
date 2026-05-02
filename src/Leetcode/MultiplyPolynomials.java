package Leetcode;//Problem Link: https://leetcode.com/problems/multiply-two-polynomials/

/**
 * Solution for multiplying two polynomials using Fast Fourier Transform (FFT).
 *
 * Problem: Given two polynomials represented as coefficient arrays, multiply them. - poly1[i] = coefficient of x^i in
 * first polynomial - poly2[i] = coefficient of x^i in second polynomial
 *
 * Example: poly1 = [3, 2, 5] represents 3 + 2x + 5x² poly2 = [1, 4] represents 1 + 4x Result: (3 + 2x + 5x²)(1 + 4x) =
 * 3 + 14x + 13x² + 20x³ Output: [3, 14, 13, 20]
 *
 * Naive approach: O(n²) - multiply each term with every other term FFT approach: O(n log n) - use convolution theorem
 *
 * Key insight: Convolution Theorem - Polynomial multiplication is equivalent to convolution in coefficient space -
 * Convolution in time domain = pointwise multiplication in frequency domain - FFT transforms between time and frequency
 * domains in O(n log n)
 *
 * Algorithm: 1. Pad both polynomials to size 2^k where 2^k ≥ n + m 2. Convert polynomials to point-value representation
 * using FFT 3. Multiply point-values element-wise (O(n)) 4. Convert back to coefficient representation using inverse
 * FFT 5. Round results to get final coefficients
 *
 * Why FFT? - Polynomial of degree n-1 is uniquely determined by n points - Multiplication in point-value form is O(n)
 * (just multiply y-values) - FFT efficiently converts between coefficient and point-value forms
 *
 * Mathematical background: - We evaluate polynomials at n-th roots of unity: e^(2πik/n) for k = 0,1,...,n-1 - These
 * points have special properties that make FFT work - Inverse FFT recovers coefficients from point-values
 *
 * Time Complexity: O((n + m) log(n + m)) Space Complexity: O(n + m)
 */
public class MultiplyPolynomials {

    /**
     * Multiplies two polynomials using FFT.
     *
     * @param poly1 Coefficients of first polynomial [a₀, a₁, ..., aₙ₋₁]
     * @param poly2 Coefficients of second polynomial [b₀, b₁, ..., bₘ₋₁]
     * @return Coefficients of product polynomial
     */
    public long[] multiply(int[] poly1, int[] poly2) {

        var n = poly1.length;
        var m = poly2.length;

        // ================================================================
        // STEP 1: Pad to next power of 2
        // ================================================================
        // FFT requires size to be power of 2 for divide-and-conquer
        // Product polynomial has degree (n-1) + (m-1) = n + m - 2
        // So we need at least n + m - 1 coefficients
        var nextPower2 = 1 << ((int) Math.ceil(Math.log(n + m) / Math.log(2)));

        ComplexNumber a[] = new ComplexNumber[nextPower2];
        ComplexNumber b[] = new ComplexNumber[nextPower2];

        // Initialize with zeros (padding)
        for (int i = 0; i < nextPower2; i++) {
            a[i] = new ComplexNumber(0, 0);
            b[i] = new ComplexNumber(0, 0);
        }

        // Copy polynomial coefficients to real part of complex numbers
        for (int i = 0; i < n; i++) {
            a[i].real = poly1[i];
        }

        for (int i = 0; i < m; i++) {
            b[i].real = poly2[i];
        }

        // ================================================================
        // STEP 2: Apply FFT to transform to point-value representation
        // ================================================================
        // FFT evaluates polynomial at n-th roots of unity
        // inverse = 1 means forward transform (coefficients → point-values)
        a = fft(a, nextPower2, 1);
        b = fft(b, nextPower2, 1);

        // ================================================================
        // STEP 3: Pointwise multiplication in frequency domain
        // ================================================================
        // In point-value representation, multiplication is O(n)
        // Just multiply corresponding y-values
        for (int i = 0; i < nextPower2; i++) {
            a[i] = a[i].prod(b[i]);
        }

        // ================================================================
        // STEP 4: Apply inverse FFT to get coefficients
        // ================================================================
        // inverse = -1 means inverse transform (point-values → coefficients)
        a = fft(a, nextPower2, -1);

        // ================================================================
        // STEP 5: Extract and round results
        // ================================================================
        var res = new long[m + n - 1];

        // Divide by n to normalize (inverse FFT property)
        // Round to handle floating-point precision errors
        for (int i = 0; i < m + n - 1; i++) {
            res[i] = Math.round(a[i].real / nextPower2);
        }
        return res;
    }


    /**
     * Fast Fourier Transform using Cooley-Tukey algorithm.
     *
     * Evaluates polynomial at n-th roots of unity using divide-and-conquer.
     *
     * Algorithm (Cooley-Tukey): 1. Base case: n = 1, return as-is 2. Divide: Split into even and odd indexed
     * coefficients 3. Conquer: Recursively apply FFT to both halves 4. Combine: Use butterfly operations to merge
     * results
     *
     * Mathematical foundation: - A polynomial of degree n-1: P(x) = a₀ + a₁x + a₂x² + ... + aₙ₋₁x^(n-1) - Split into
     * even and odd powers: P(x) = (a₀ + a₂x² + a₄x⁴ + ...) + x(a₁ + a₃x² + a₅x⁴ + ...) P(x) = P_even(x²) + x ·
     * P_odd(x²)
     *
     * - Evaluate at ωₙᵏ (k-th n-th root of unity): P(ωₙᵏ) = P_even(ωₙ²ᵏ) + ωₙᵏ · P_odd(ωₙ²ᵏ)
     *
     * - Key property: ωₙ²ᵏ = ωₙ/₂ᵏ (doubling property) - Also: ωₙᵏ⁺ⁿ/² = -ωₙᵏ (symmetry property)
     *
     * Butterfly operation: - res[k] = even[k] + ωₙᵏ · odd[k] - res[k + n/2] = even[k] - ωₙᵏ · odd[k]
     *
     * @param arr Array of complex numbers (polynomial coefficients or point-values)
     * @param n Size of array (must be power of 2)
     * @param inverse 1 for forward FFT, -1 for inverse FFT
     * @return Transformed array (point-values or coefficients)
     */
    public ComplexNumber[] fft(ComplexNumber[] arr, int n, int inverse) {

        // ================================================================
        // Base case: single element
        // ================================================================
        if (n == 1) {
            return arr;
        }

        var half = n >> 1;  // n / 2

        // ================================================================
        // Divide: Split into even and odd indexed coefficients
        // ================================================================
        ComplexNumber even[] = new ComplexNumber[half];
        ComplexNumber odd[] = new ComplexNumber[half];

        for (int j = 0, i = 0; i < n; j++, i += 2) {
            even[j] = arr[i];      // arr[0], arr[2], arr[4], ...
            odd[j] = arr[i + 1];   // arr[1], arr[3], arr[5], ...
        }

        // ================================================================
        // Conquer: Recursively apply FFT to both halves
        // ================================================================
        even = fft(even, half, inverse);
        odd = fft(odd, half, inverse);

        // ================================================================
        // Combine: Use butterfly operations
        // ================================================================
        var res = new ComplexNumber[n];
        for (int i = 0; i < n; i++) {
            res[i] = new ComplexNumber(0, 0);
        }

        for (int i = 0; i < half; i++) {
            // Twiddle factor: ωₙⁱ (i-th n-th root of unity)
            // inverse = 1 → forward FFT, inverse = -1 → inverse FFT
            var omega = omega(n, i * inverse);

            var evenPart = even[i];
            var oddPart = odd[i].prod(omega);  // odd[i] · ωₙⁱ

            // Butterfly operations using symmetry property
            res[i] = evenPart.add(oddPart);          // even[i] + ωₙⁱ · odd[i]
            res[i + half] = evenPart.sub(oddPart);   // even[i] - ωₙⁱ · odd[i]
        }

        return res;
    }

    /**
     * Computes the j-th n-th root of unity.
     *
     * n-th roots of unity are complex numbers that satisfy z^n = 1. They are evenly spaced on the unit circle in the
     * complex plane.
     *
     * Formula: ωₙʲ = e^(2πij/n) = cos(2πj/n) + i·sin(2πj/n)
     *
     * Where: - n = total number of roots - j = index of the root (0 ≤ j < n)
     *
     * Properties: - ωₙ⁰ = 1 (identity) - ωₙⁿ = 1 (periodicity) - ωₙʲ⁺ⁿ/² = -ωₙʲ (symmetry - opposite side of circle) -
     * (ωₙʲ)ᵏ = ωₙʲᵏ (power property)
     *
     * Example: 4th roots of unity (n=4) - ω₄⁰ = 1 (angle = 0°) - ω₄¹ = i (angle = 90°) - ω₄² = -1 (angle = 180°) - ω₄³
     * = -i (angle = 270°)
     *
     * @param n Total number of roots
     * @param j Index of the root
     * @return j-th n-th root of unity as complex number
     */
    public ComplexNumber omega(double n, double j) {

        // Angle in radians: 2πj/n
        double angle = (2 * Math.PI * j) / n;

        // Euler's formula: e^(iθ) = cos(θ) + i·sin(θ)
        return new ComplexNumber(Math.cos(angle), Math.sin(angle));
    }

    /**
     * Represents a complex number with real and imaginary parts.
     *
     * Complex number: z = a + bi - a = real part - b = imaginary part - i = √(-1), where i² = -1
     *
     * Used for FFT calculations to represent polynomial evaluations at complex roots of unity.
     */
    class ComplexNumber {

        double real;  // Real part (a)
        double img;   // Imaginary part (b)

        /**
         * Constructor for complex number.
         *
         * @param real Real part
         * @param img Imaginary part
         */
        public ComplexNumber(double real, double img) {

            this.real = real;
            this.img = img;
        }

        /**
         * Adds two complex numbers.
         *
         * Formula: (a + bi) + (c + di) = (a + c) + (b + d)i
         *
         * @param number Number to add
         * @return Sum of the two complex numbers
         */
        public ComplexNumber add(ComplexNumber number) {

            return new ComplexNumber(real + number.real, img + number.img);
        }

        /**
         * Subtracts two complex numbers.
         *
         * Formula: (a + bi) - (c + di) = (a - c) + (b - d)i
         *
         * @param number Number to subtract
         * @return Difference of the two complex numbers
         */
        public ComplexNumber sub(ComplexNumber number) {

            return new ComplexNumber(real - number.real, img - number.img);
        }

        /**
         * Multiplies two complex numbers.
         *
         * Formula: (a + bi)(c + di) = (ac - bd) + (ad + bc)i
         *
         * Derivation: - (a + bi)(c + di) - = ac + adi + bci + bdi² - = ac + adi + bci - bd  (since i² = -1) - = (ac -
         * bd) + (ad + bc)i
         *
         * Real part: ac - bd Imaginary part: ad + bc
         *
         * @param number Number to multiply
         * @return Product of the two complex numbers
         */
        public ComplexNumber prod(ComplexNumber number) {

            return new ComplexNumber(real * number.real - img * number.img, real * number.img + img * number.real);
        }
    }
}