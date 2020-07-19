package java_preproc_macros;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;


public class Java_Preproc_Macros {

    private Java_Preproc_Macros() {
    }

    @Target(ElementType.TYPE)
    public @interface Using_Preproc_Macros {
    }
    
    static public boolean agent_found = false;

    static public HashSet<String> handled_class_names = new HashSet<>();

    public static void premain(String agentArgs, Instrumentation inst) {

        Java_Preproc_Macros.agent_found = true;

        try {

            Preproc_Macros_ClassFileTransformer transformer = new Preproc_Macros_ClassFileTransformer();
            inst.addTransformer(transformer, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public class Preproc_Macros_ClassFileTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            byte[] byteCode = classfileBuffer;

            if (className == null)
                return byteCode;

            String class_name_with_dots = className.replace("/", ".");
            
            if (!class_name_with_dots.startsWith("java_preproc_macros") && (class_name_with_dots.startsWith("java") || class_name_with_dots.startsWith("sun"))) {
                return byteCode;
            }

            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass;
            try {
                ctClass = classPool.get(class_name_with_dots);
            } catch (NotFoundException e) {
                return byteCode;
            }

            try {

                if (handled_class_names.contains(class_name_with_dots))
                    return byteCode;

                if (ctClass.hasAnnotation(Using_Preproc_Macros.class)) {

                    CtMethod[] methods = ctClass.getDeclaredMethods();

                    for (CtMethod cm : methods) {
                        cm.instrument(new ExprEditor() {
                            public void edit(MethodCall m) throws CannotCompileException {
                                // __LINE__
                                if (m.getMethodName().equals("__LINE__")) {
                                    m.replace("{ $_ = " + m.getLineNumber() + "; }");
                                }
                                // __FILE__
                                else if (m.getMethodName().equals("__FILE__")) {
                                    m.replace("{ $_ = \"" + m.getFileName() + "\"; }");
                                }
                                // __FUNC__
                                else if (m.getMethodName().equals("__FUNC__")) {
                                    m.replace("{ $_ = \"" + m.where().getName() + "\"; }");
                                }
                                // __CLASS__
                                else if (m.getMethodName().equals("__CLASS__")) {
                                    m.replace("{ $_ = \"" + m.getEnclosingClass().getName() + "\"; }");
                                }
                                // __TIME__
                                else if (m.getMethodName().equals("__TIME__")) {
                                    m.replace("{ $_ = \"" + (new SimpleDateFormat("hh:mm:ss").format(new Date()))
                                            + "\"; }");
                                }
                                // __DATE__
                                else if (m.getMethodName().equals("__DATE__")) {
                                    m.replace("{ $_ = \"" + (new SimpleDateFormat("MMM d yyyy").format(new Date()))
                                            + "\"; }");
                                }

                            }
                        });
                    }

                    handled_class_names.add(class_name_with_dots);

                    byteCode = ctClass.toBytecode();
                    ctClass.detach();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return byteCode;
        }

    }

    static public String __FILE__() {
        issue_warning();
        StackTraceElement[] traces = (new Throwable()).getStackTrace();
        return traces[1].getFileName();
    }

    static public int __LINE__() {
        issue_warning();
        StackTraceElement[] traces = (new Throwable()).getStackTrace();
        return traces[1].getLineNumber();
    }

    static public String __FUNC__() {
        issue_warning();
        StackTraceElement[] traces = (new Throwable()).getStackTrace();
        return traces[1].getMethodName();
    }

    static public String __CLASS__() {
        issue_warning();
        StackTraceElement[] traces = (new Throwable()).getStackTrace();
        return traces[1].getClassName();
    }

    static public String __DATE__() {
        issue_warning();
        return new SimpleDateFormat("MMM d yyyy").format(new Date());
    }

    static public String __TIME__() {
        issue_warning();
        return new SimpleDateFormat("hh:mm:ss").format(new Date());
    }

    static boolean agent_warning_issues = false;
    static HashSet<String> class_names_for_warning_reports = new HashSet<>();

    static void issue_warning() {

        if (!agent_found && !agent_warning_issues) {

            String warning = "[WARNING] agent java_preproc_macros.Java_Preproc_Macros was not attached to the JVM!";
            System.err.println(warning);
            agent_warning_issues = true;
        }

        StackTraceElement[] traces = (new Throwable()).getStackTrace();
        StackTraceElement t = traces[2];
        String class_name = t.getClassName();

        if (!class_names_for_warning_reports.contains(class_name)) {


            try {
                Class<?> clazz = Class.forName(class_name);

                if (!clazz.isAnnotationPresent(Using_Preproc_Macros.class)) {

                    String warning = String.format("[WARNING] the class %s does not have the @"+Using_Preproc_Macros.class.getSimpleName()+" annotation.", class_name);
                    warning += "\nIt will fallback to figuring things out at runtime which is slow!\n";
                    
                    System.err.println(warning);
                    class_names_for_warning_reports.add(class_name);

                    new Throwable().printStackTrace();

                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
