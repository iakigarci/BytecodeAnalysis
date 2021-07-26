
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

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public Map<MethodReport, List<MethodReport>> start() {
        method = new MethodReport(mg.getName(), mg.getClassName(), mg.getReturnType().toString());
        if (JCallGraph.isPackage(mg.getClassName()) && JCallGraph.isPackage(visitedClass.getClassName())) {
            if (mg.isAbstract() || mg.isNative())
                return (HashMap<MethodReport, List<MethodReport>>) Collections.<MethodReport, List<MethodReport>>emptyMap();

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
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
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
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
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
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
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
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
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
                    MethodGen mgAUx = new MethodGen(methodAux, visitedClass.getClassName(), constants);
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
}
