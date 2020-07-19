package java_preproc_macros_examples;

import static java_preproc_macros.Java_Preproc_Macros.*;

@Using_Preproc_Macros
public class Java_Preproc_Macro_Example_01 {

    public static void main(String[] args) {
        
        System.out.println("__LINE__ "+__LINE__());
        System.out.println("__FILE__ "+__FILE__());
        System.out.println("__CLASS__ "+__CLASS__());
        System.out.println("__FUNC__ "+__FUNC__());
        System.out.println("__DATE__ "+__DATE__());
        System.out.println("__TIME__ "+__TIME__());
        System.out.println("__LINE__ "+__LINE__());

    }
    
}