/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package additives;

import econom.Country;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author kmt
 */
public class ReadFile {

    private String csvFile = "C:\\Users\\kmt\\Documents\\MEGA\\Coding\\Java\\econom\\iso-codes.csv";
    private String line = "";
    private String cvsSplitBy = ";";
    private List<Country> countries;

    public ReadFile(String csvFile) {
        this.csvFile = csvFile;
        countries = new ArrayList<>();

    }
    
    
    public List<Country> countries(){

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use ; as separator
                String[] country = line.split(cvsSplitBy);
                
                

                System.out.println("Country [code= " + country[0] + " , name=" + country[1] + "]");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
       
        return countries; 

    }
}
