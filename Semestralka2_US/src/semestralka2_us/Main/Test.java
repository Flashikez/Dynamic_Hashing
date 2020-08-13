/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semestralka2_us.Main;

import semestralka2_us.Nehnutelnost.Nehnutelnost;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import semestralka2_us.Dynamic_hashing;

/**
 *
 * @author MarekPC
 */
public class Test {

    public static void test(int iterations, double insertChance, int hashSize, int dataBlockFactor, int overFillBlockFactor) {
        try {
            Random r = new Random();
            long seed = r.nextInt(100000);
            System.out.println("SEED " + seed);
            r.setSeed(seed);
            Dynamic_hashing hashing = new Dynamic_hashing("data.bin", "over.bin", hashSize, dataBlockFactor, overFillBlockFactor, Nehnutelnost.class);
            int testNum = iterations;
            ArrayList<Nehnutelnost> arr = new ArrayList<>(testNum);
            for (int i = 0; i < testNum; i++) {
                Nehnutelnost n = new Nehnutelnost(r.nextInt(100), r.nextInt(100), "Bratislava", "Lala");
                if (r.nextDouble() <= insertChance) {
                    if (hashing.insert(n) != false) {
//                        System.out.println("ADDING "+i);
                        arr.add(n);
                    }
                } else {
                    if (arr.size() > 0) {
                        int index = r.nextInt(arr.size());

                        Nehnutelnost ne = arr.get(index);
                        arr.remove(ne);
                        hashing.delete(ne);
                    }

                }

            }
            if (hashing.chainLengthCheck() == false) {
                System.err.println("LENGTH OF COLISION CHAIN DIFFERS");
                System.exit(10);
            }
            for (Nehnutelnost nehnutelnost : arr) {
                Nehnutelnost found = hashing.find(nehnutelnost);
                if (found == null) {
                    System.out.println(nehnutelnost);
                    System.err.println("FAIL");
                    System.exit(50);
                } else {
                    if (!found.equalsOther(found)) {
                        System.out.println(nehnutelnost);
                        System.err.println("FAIL");
                        System.exit(20);
                    }

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Semestralka2_US.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
