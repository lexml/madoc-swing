/*******************************************************************************
 * 2008-2012 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 *
 * Writing of numerals in Portuguese Language
 * Implemented according to the Portuguese grammar
 * Supports any unit and any decimal precision
 *
 * Portuguese Gramar Rules:
 * i) A conjuncao "e" e' sempre intercalada entre as centenas, as dezenas e as unidades:
 * a) novecentos e noventa e nove Euros.
 *
 * ii) Nao se emprega a conjuncao entre os milhares e as centenas, salvo quando o numero
 * terminar numa centena com dois zeros:
 * a) mil duzentos e trinta euros;
 * b) mil e duzentos.
 *
 * iii) Em numeros muito grandes (acima do milhao?), a conjuncao "e" emprega-se entre os membros
 * da mesma ordem de unidades, e omite-se quando se passa de uma ordem a outra:
 * a) duzentos e noventa e tres mil quinhentos e setenta e dois;
 * b) trezentos e trinta e dois bilioes, quatrocentos e quinze milhões, setecentos e quarenta e um mil duzentos e onze.
 *
 * Nova Gramatica do Portugues Contemporaneo, Edicoes Joao Sa da Costa
 *
 *******************************************************************************/
package org.dma.utils.java.numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Numerals {
	
		public static final String[][] UNIT_REAL=new String[][]{{"real","reais"},{"centavo","centavos"}};
        public static final String[][] UNIT_EURO=new String[][]{{"euro","euros"},{"cêntimo","cêntimos"}};
        public static final String[][] UNIT_METER=new String[][]{{"metro","metros"},{"centímetro","centímetros"}};
        public static final String[][] UNIT_DAY=new String[][]{{"dia","dias"},{"",""}};
        public static final String[][] UNIT_NOTHING=new String[][]{{"",""},{"",""}};

        public static final String CONJUCTION_AND = "e";
        public static final String CONJUCTION_OF = "de";
        
        public static final String QUALIFIERS[][] = {
                {"mil", "mil"}, {"milhão", "milhões"},
                {"bilhão", "bilhões"}, {"trilhão", "trilhões"},
                {"quatrilhão", "quatrilhões"}, {"quintilhão", "quintilhões"},
                {"sextilhão", "sextilhões"}, {"septilhão", "septilhões"},
                {"octilhão", "octilhões"}, {"nonilhão", "nonilhões"} };

        public static final String NUMERALS_MASCULINO[][] = {
                //0-19
                {"zero", "um", "dois", "três", "quatro", "cinco",
                "seis", "sete", "oito", "nove", "dez",
                "onze", "doze", "treze", "catorze", "quinze",
                "dezesseis", "dezessete", "dezoito", "dezenove"},
                //20-90
                {"vinte", "trinta", "quarenta", "cinquenta",
                "sessenta", "setenta", "oitenta", "noventa"},
                //100-900
                {"cem", "cento",
                "duzentos", "trezentos", "quatrocentos", "quinhentos",
                "seiscentos", "setecentos", "oitocentos", "novecentos"}
        };
        
        public static final String NUMERALS_FEMININO[][] = {
        	//0-19
            {"zero", "uma", "duas", "três", "quatro", "cinco",
            "seis", "sete", "oito", "nove", "dez",
            "onze", "doze", "treze", "catorze", "quinze",
            "dezesseis", "dezessete", "dezoito", "dezenove"},
            //20-90
            {"vinte", "trinta", "quarenta", "cinquenta",
            "sessenta", "setenta", "oitenta", "noventa"},
            //100-900
            {"cem", "cento",
            "duzentas", "trezentas", "quatrocentas", "quinhentas",
            "seiscentas", "setecentas", "oitocentas", "novecentas"}
        };
        
        private String numerals[][] = NUMERALS_MASCULINO;
        

        //number of decimals
        private final int decimals;
        //unit label
        private final String[][] unit;
        
        /*
         * CONSTRUTORES
         */

        public Numerals(int decimals, String[][] unit) {
                this.decimals=decimals;
                this.unit=unit;
        }

        public Numerals(int decimals) {
            this(decimals, UNIT_NOTHING);
        }
        
        public void setNumeralsString(String numerals[][]){
        	this.numerals = numerals;
        }
    

        public String toString(BigDecimal value) {
        	return toString(value, false);
        }
        
        public String toString(BigDecimal value, boolean parentesis) {

                String s="";

                try{
                        value=value.abs().setScale(decimals, RoundingMode.HALF_EVEN);

                        //retrieves integer and decimal parts
                        BigInteger integer_part=value.abs().toBigInteger();
                        //(value-integer_part)*(10^decimais)
                        BigInteger decimal_part=((value.subtract(new BigDecimal(integer_part))).
                                        multiply(new BigDecimal(10).pow(decimals))).toBigInteger();

                        //converts integer part
                        if (integer_part.doubleValue()>0)
                                s+=ordersToString(getOrders(integer_part),unit[0], parentesis);

                        //converts decimal part
                        if (decimal_part.doubleValue()>0){
                                s+=s.length()>0 ? " "+CONJUCTION_AND+" " : "";
                                s+=ordersToString(getOrders(decimal_part),unit[1], parentesis);
                        }

                } catch (Exception e){
                        e.printStackTrace();
                }

                return s.trim();

        }


        /*
         * Dismembers value into orders
         */
        private List<Integer> getOrders(BigInteger value) {

                List<Integer> orders=new ArrayList<Integer>();

                try{
                        while(value.doubleValue()>=1000) {
                                orders.add(value.remainder(BigInteger.valueOf(1000)).intValue());
                                value=value.divide(BigInteger.valueOf(1000));
                        }
                        orders.add(value.intValue());

                } catch (Exception e){
                        e.printStackTrace();
                }

                return orders;

        }


        /*
         * Concatenates all the orders
         */
        private String ordersToString(List<Integer> orders, String[] unit, boolean parentesis) {

                String s="";

                try{
                        Integer order0=orders.get(0);

                        //processes below 999
                        if (order0>0) s+=orderToString(order0);

                        //last processed order
                        int last=0;

                        //processes above 999
                        for (int i=1; i<orders.size(); i++){

                                Integer value=orders.get(i);

                                if (value!=0){

                                        //singular and plural
                                        String q=QUALIFIERS.length>=i ? QUALIFIERS[i-1][value>1?1:0] : "?";
                                        /*
                                         * ordem actual >= MILHOES
                                         * ordem anterior = CENTENAS
                                         * nao existem centenas
                                         * (MILHOES DE; BILIOES DE; etc)
                                         */
                                        if (i>=2 && last==0 && order0==0) q+=" "+CONJUCTION_OF+" ";
                                        /*
                                         * ordem anterior = CENTENAS
                                         * existem centenas (EVITA MIL E ?)
                                         * centenas inferiores a 100 (E UM; E DOIS; etc)
                                         * centenas multiplas de 100 (E CEM; E DUZENTOS; etc)
                                         */
                                        else if (last==0 && order0>0 && (order0<100 || order0%100==0))
                                                q+=" "+CONJUCTION_AND+" ";
                                        /*
                                         * ordem actual >= MILHOES
                                         * (SEPARA MILHOES, BILIOES, etc)
                                         */
                                        else if (i>=2) q+=", ";

                                        else q+=" ";

                                        /*
                                         * ordem actual = MILHARES
                                         * milhares = 1
                                         * (EVITA "UM MIL")
                                         */
                                        if (i==1 && value==1)
                                                s=q+s;
                                        else
                                                s=orderToString(value)+q+s;

                                        last=i;

                                }

                        }

                        //parentesis
                        if (parentesis){
                        	s = "("+s.replaceAll(" $", "")+") ";
                        }
                        
                        //para evitar "UM" + PLURAL da moeda
                        if (orders.size()==1 && order0==1)
                                s+=unit[0];
                        else
                                s+=unit[1];

                } catch (Exception e){
                        e.printStackTrace();
                }

                return s;
        }


        /*
         * Creates order string
         */
        private String orderToString(Integer value) {

                String s="";

                try{
                        //processa ate' 19
                        if (value<20){
                                s+=numerals[0][value]+" ";

                        //processa de 20 a 99
                        }else if (value<100){
                                s+=numerals[1][value/10-2]+" ";
                                if (value % 10!=0) s+=CONJUCTION_AND+" "+orderToString(value % 10);

                        //processa 100
                        }else if (value==100){
                                s+=numerals[2][0]+" ";

                        //processa de 101 a 999
                        }else if (value<1000){
                                s+=numerals[2][value/100]+" ";
                                if (value % 100!=0) s+=CONJUCTION_AND+" "+orderToString(value % 100);
                        }

                } catch (Exception e){
                        e.printStackTrace();
                }

                return s;
        }


        public static void main(String[] argvs){

        		System.out.println(new Numerals(2, Numerals.UNIT_DAY).toString(new BigDecimal(125), true));
        	
                /*
                Numerals numerals=new Numerals(2, Numerals.UNIT_DAY);

                //teste dos exemplos apresentados em "Nova Gramatica do Portugues Contemporaneo"
                final BigDecimal[] values={
                        new BigDecimal("999"),
                        new BigDecimal("1230"), new BigDecimal("1200"),
                        new BigDecimal("293572"), new BigDecimal("332415741211")};

                for(int i=0; i<values.length; i++){
                        System.out.println(values[i]+": "+ numerals.toString(values[i]));
                }

                //teste de intervalos
                final double[][] intervals=new double[][]{
                        {0.0,0.09,.01}, {0.90,0.99,.01},
                        {1,1.99,.01}, {2,2.99,.1}, {3,200,1},
                        {999,1001,1}, {2000,2001,1}, {1230,1231,1},
                        {9999,10001,1}, {101100,101101,1}, {123450,123451,1},
                        {999999,1000001,1}, {10010099,10010101,1}, {12345670,12345671,1},
                        {999999999,1000000001,1}, {1000567099,1000567101,1}, {1000010000,1000010002,1},
                        {1001000000,1001000002,1}, {1010010000,1010010002,1}, {1234567890,1234567891,1} };

                for(int i=0; i<intervals.length; i++){
                        System.out.println();
                        System.out.println("===INTERVALO #"+(i+1)+"===");
                        for(BigDecimal j=BigDecimal.valueOf(intervals[i][0]); j.doubleValue()<=intervals[i][1]; j=j.add(BigDecimal.valueOf(intervals[i][2]))){
                                System.out.println(numerals.toString(j));
                        }
                }
                */

        }


}
