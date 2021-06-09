
/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package callgraph;

import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.generic.*;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.MethodInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.bcel.classfile.Method;

/**
 * The simplest of method visitors, prints any invoked method signature for all
 * method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */
public class MethodVisitor extends EmptyVisitor {

    JavaClass visitedClass;
    private MethodGen mg; // metodo que se esta analizando
    private ConstantPoolGen cp;
    private MethodReport method;
    private List<MethodReport> methodCalls = new ArrayList<>();
    private ConstantPoolGen constants;
    private static CalledFromList cfl;

    public MethodVisitor(MethodGen m, JavaClass jc, ConstantPoolGen cpg) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        constants = cpg;
        cfl = CalledFromList.getCalledfromlist();
    }

    private String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }

    public HashMap<MethodReport, List<MethodReport>> start() {
        int lineaClase = -1;
        int LOC = -1;
        if (mg.getLineNumbers().length != 0) {
            lineaClase = (mg.getLineNumbers()[0].getSourceLine()) - 1;
            LOC = getLineNumbers(lineaClase, mg);
        }
        method = new MethodReport(mg.getName(), mg.getClassName(), 0, mg.getReturnType().toString(), 0, "A");

        if (JCallGraph.isPackage(mg.getClassName()) && JCallGraph.isPackage(visitedClass.getClassName())) {
            if (mg.isAbstract() || mg.isNative())
                return (HashMap<MethodReport, List<MethodReport>>) Collections.emptyList();

            for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
                Instruction i = ih.getInstruction();

                if (!visitInstruction(i))
                    i.accept(this);
            }
        }
        HashMap<MethodReport, List<MethodReport>> map = new HashMap<>();
        map.put(method, methodCalls);
        return map; // return the method call list by each method.
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConst.getInstruction(opcode) != null) && !(i instanceof ConstantPushInstruction)
                && !(i instanceof ReturnInstruction));
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        if (JCallGraph.isPackage(i.getReferenceType(cp).toString())) {
            if (i.getClassName(cp).equals(this.visitedClass.getClassName())) {
                Method methodAux = this.getMethod(i.getMethodName(cp));
                if (methodAux != null) {
                    int sourceLine = -1;
                    int LOC = -1;
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
                    if (mgAUx.getLineNumbers().length != 0) {
                        sourceLine = mgAUx.getLineNumbers()[0].getSourceLine() - 1;
                        LOC = getLineNumbers(sourceLine, mgAUx);
                    }
                    MethodReport m = new MethodReport(mgAUx.getName(), mgAUx.getClassName(), 0,
                            mgAUx.getReturnType().toString(), 0, "A");
                    if (JCallGraph.isExactSubsecuence(i.getReferenceType(cp).toString(),
                            this.visitedClass.getClassName())) {
                        methodCalls.add(m);

                    }
                }
            } else {
                cfl = CalledFromList.getCalledfromlist();
                cfl.addToList(i.getReferenceType(cp).toString() + i.getMethodName(cp),
                        method.getPaquete() + method.getNombre());
            }
        }
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) { // metodo que se visita desde mg
        if (JCallGraph.isPackage(i.getReferenceType(cp).toString())) {
            if (i.getClassName(cp).equals(this.visitedClass.getClassName())) {
                Method methodAux = this.getMethod(i.getMethodName(cp));
                if (methodAux != null) {
                    int sourceLine = -1;
                    int LOC = -1;
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
                    if (mgAUx.getLineNumbers().length != 0) {
                        sourceLine = mgAUx.getLineNumbers()[0].getSourceLine() - 1;
                        LOC = getLineNumbers(sourceLine, mgAUx);
                    }
                    MethodReport m = new MethodReport(mgAUx.getName(), mgAUx.getClassName(), 0,
                            mgAUx.getReturnType().toString(), 0, "A");
                    if (JCallGraph.isExactSubsecuence(i.getReferenceType(cp).toString(),
                            this.visitedClass.getClassName())) {
                        methodCalls.add(m);

                    }
                }
            } else {
                cfl = CalledFromList.getCalledfromlist();
                cfl.addToList(i.getReferenceType(cp).toString() + i.getMethodName(cp),
                        method.getPaquete() + method.getNombre());
            }
        }
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        if (JCallGraph.isPackage(i.getReferenceType(cp).toString())) {
            if (i.getClassName(cp).equals(this.visitedClass.getClassName())) {
                Method methodAux = this.getMethod(i.getMethodName(cp));
                if (methodAux != null) {
                    int sourceLine = -1;
                    int LOC = -1;
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
                    if (mgAUx.getLineNumbers().length != 0) {
                        sourceLine = mgAUx.getLineNumbers()[0].getSourceLine() - 1;
                        LOC = getLineNumbers(sourceLine, mgAUx);
                    }
                    MethodReport m = new MethodReport(mgAUx.getName(), mgAUx.getClassName(), 0,
                            mgAUx.getReturnType().toString(), 0, "A");
                    if (JCallGraph.isExactSubsecuence(i.getReferenceType(cp).toString(),
                            this.visitedClass.getClassName())) {
                        methodCalls.add(m);

                    }
                }
            } else {
                cfl = CalledFromList.getCalledfromlist();
                cfl.addToList(i.getReferenceType(cp).toString() + i.getMethodName(cp),
                        method.getPaquete() + method.getNombre());
            }
        }
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        if (JCallGraph.isPackage(i.getReferenceType(cp).toString())) {
            if (i.getClassName(cp).equals(this.visitedClass.getClassName())) {
                Method methodAux = this.getMethod(i.getMethodName(cp));
                if (methodAux != null) {
                    int sourceLine = -1;
                    int LOC = -1;
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
                    if (mgAUx.getLineNumbers().length != 0) {
                        sourceLine = mgAUx.getLineNumbers()[0].getSourceLine() - 1;
                        LOC = getLineNumbers(sourceLine, mgAUx);
                    }
                    MethodReport m = new MethodReport(mgAUx.getName(), mgAUx.getClassName(), 0,
                            mgAUx.getReturnType().toString(), 0, "A");
                    if (JCallGraph.isExactSubsecuence(i.getReferenceType(cp).toString(),
                            this.visitedClass.getClassName())) {
                        methodCalls.add(m);

                    }
                }
            } else {
                cfl = CalledFromList.getCalledfromlist();
                cfl.addToList(i.getReferenceType(cp).toString() + i.getMethodName(cp),
                        method.getPaquete() + method.getNombre());
            }
        }
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        if (JCallGraph.isPackage(i.getReferenceType(cp).toString())) {
            if (i.getClassName(cp).equals(this.visitedClass.getClassName())) {
                Method methodAux = this.getMethod(i.getMethodName(cp));
                if (methodAux != null) {
                    int sourceLine = -1;
                    int LOC = -1;
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
                    if (mgAUx.getLineNumbers().length != 0) {
                        sourceLine = mgAUx.getLineNumbers()[0].getSourceLine() - 1;
                        LOC = getLineNumbers(sourceLine, mgAUx);
                    }
                    MethodReport m = new MethodReport(mgAUx.getName(), mgAUx.getClassName(), 0,
                            mgAUx.getReturnType().toString(), 0, "A");
                    if (JCallGraph.isExactSubsecuence(i.getReferenceType(cp).toString(),
                            this.visitedClass.getClassName())) {
                        methodCalls.add(m);

                    }
                }
            } else {
                cfl = CalledFromList.getCalledfromlist();
                cfl.addToList(i.getReferenceType(cp).toString() + i.getMethodName(cp),
                        method.getPaquete() + method.getNombre());
            }
        }
    }

    public boolean isVisitable() {
        if (mg.isAbstract() || mg.isNative()) {
            return false;
        }
        return true;
    }

    private Method getMethod(String name) {
        for (Method method : visitedClass.getMethods()) {
            if (method.getName().contains(name)) {
                return method;
            }
        }
        return null;
    }

    private int getLineNumbers(int sourceLine, MethodGen pMg) {
        // LineNumberGen[] lng = pMg.getLineNumbers();
        // int lineNumbers = lng[lng.length - 1].getSourceLine() - sourceLine;
        // if (lng[lng.length - 1].getInstruction().toString().contains(" return")) {
        //     lineNumbers = lng[lng.length - 2].getSourceLine() - sourceLine;
        //     if (lng[lng.length - 2].getInstruction().getNext()!=null) {
        //         lineNumbers++;
        //     }
        // }
        // // else if (lng[lng.length-1].getInstruction().getNext()!=null) {
        // // lineNumbers++;
        // // }
        // if (lineNumbers==0) {
        //     return 1;
        // }
        // return lineNumbers;
        return 0;
    }

    // private int getLastLine(MethodGen pMg) {
    //     LineNumberGen[] lng = pMg.getLineNumbers();
    //     int lastLine = lng[lng.length - 1].getSourceLine();
    //     if (lng[lng.length - 1].getInstruction().toString().contains(" return")) {
    //         lastLine = lng[lng.length - 2].getSourceLine();
    //         if (lng[lng.length - 2].getInstruction().getNext()!=null) {
    //             lastLine++;
    //         }
    //     }
    //     return 
    // }
}
